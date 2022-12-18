package com.restkeeper.order.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.aop.TenantAnnotation;
import com.restkeeper.constants.OrderDetailType;
import com.restkeeper.constants.OrderPayType;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.dto.CreditDTO;
import com.restkeeper.dto.DetailDTO;
import com.restkeeper.entity.OrderDetailEntity;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.order.mapper.OrderMapper;
import com.restkeeper.service.IOrderDetailService;
import com.restkeeper.service.IOrderService;
import com.restkeeper.store.entity.*;
import com.restkeeper.store.service.*;
import com.restkeeper.tenant.TenantContext;
import com.restkeeper.utils.SequenceUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service("orderService")
@Service(version = "1.0.0", protocol = "dubbo")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements IOrderService {
    @Autowired
    @Qualifier("orderDetailService")
    private IOrderDetailService orderDetailService;
    @Reference(version = "1.0.0", check = false)
    private ISellCalculationService sellCalculationService;

    @Override
    @Transactional
    public String addOrder(OrderEntity orderEntity) {
        //判断订单有无流水号
        if (StringUtils.isEmpty(orderEntity.getOrderNumber())) {
            //生成订单流水号
            String storeId = RpcContext.getContext().getAttachment("storeId");
            orderEntity.setOrderNumber(SequenceUtils.getSequence(storeId));
        }
        //保存到订单表,RpcContext内数据被使用
        this.saveOrUpdate(orderEntity);

        //获取订单详情
        List<OrderDetailEntity> orderDetailEntities = orderEntity.getOrderDetails();
        orderDetailEntities.forEach(orderDetailEntity -> {
            //设置订单详情类和订单的关联
            orderDetailEntity.setOrderId(orderEntity.getOrderId());
            orderDetailEntity.setOrderNumber(SequenceUtils.getSequenceWithPrefix(orderEntity.getOrderNumber()));

            //估清检查
            //得到当前菜品估量
            TenantContext.addAttachment("storeId", RpcContext.getContext().getAttachment("storeId"));
            TenantContext.addAttachment("shopId", RpcContext.getContext().getAttachment("shopId"));
            Integer count = sellCalculationService.getRemainderCount(orderDetailEntity.getDishId());
            //如果是-1,该菜品不限次数
            if (count != -1) {
                //如果当前菜品下单量超过估清量
                if (count < orderDetailEntity.getDishNumber()) {
                    throw new BussinessException(orderDetailEntity.getDishName() + "超过估清数目");
                }
                //不超过估清量,就完成扣减估清量
                sellCalculationService.decrease(orderDetailEntity.getDishId(), orderDetailEntity.getDishNumber());
            }

        });
        //保存到订单详情表
        orderDetailService.saveBatch(orderDetailEntities);

        return orderEntity.getOrderId();
    }

    @Override
    @Transactional
    @TenantAnnotation
    public boolean returnDish(DetailDTO detailDTO) {
        //查询当前订单详情信息
        OrderDetailEntity detailEntity = orderDetailService.getById(detailDTO.getDetailId());
        //得到当前订单详情信息的状态
        int dishStatus = detailEntity.getDetailStatus();
        //加菜或者正常下单的订单详情
        if (OrderDetailType.PLUS_DISH.getType() == dishStatus || OrderDetailType.NORMAL_DISH.getType() == dishStatus) {
            //判断该订单详情的菜品数
            if (detailEntity.getDishNumber() <= 0) {
                throw new BussinessException(detailEntity.getDishName() + "已经被退完");
            }

            //产生新的退菜详情
            OrderDetailEntity return_detailEntity = new OrderDetailEntity();
            BeanUtils.copyProperties(detailEntity, return_detailEntity);
            //去掉多余copy字段
            return_detailEntity.setDetailId(null);
            return_detailEntity.setShopId(null);
            return_detailEntity.setStoreId(null);

            return_detailEntity.setOrderNumber(SequenceUtils.getSequenceWithPrefix(detailEntity.getOrderNumber()));
            return_detailEntity.setDetailStatus(OrderDetailType.RETURN_DISH.getType());
            return_detailEntity.setDishNumber(1);
            return_detailEntity.setReturnRemark(detailDTO.getRemarks().toString());
            orderDetailService.save(return_detailEntity);

            //修改当前被退菜品在订单明细中的原有记录
            detailEntity.setDishNumber(detailEntity.getDishNumber() - 1);
            detailEntity.setDishAmount(detailEntity.getDishNumber() * detailEntity.getDishPrice());
            orderDetailService.updateById(detailEntity);

            //修改订单主表信息
            OrderEntity orderEntity = this.getById(detailEntity.getOrderId());
            orderEntity.setTotalAmount(orderEntity.getTotalAmount() - detailEntity.getDishPrice());
            this.updateById(orderEntity);

            //判断沽清
            Integer remainderCount = sellCalculationService.getRemainderCount(detailEntity.getDishId());
            if (remainderCount > 0) {

                //沽清中有该菜品
                //沽清数量+1
                sellCalculationService.add(detailEntity.getDishId(), 1);

            }
        } else {
            throw new BussinessException("不支持退菜操作");
        }

        return true;
    }

    @Reference(version = "1.0.0", check = false)
    private ITableService tableService;

    @Override
    @Transactional
    @TenantAnnotation
    public boolean pay(OrderEntity orderEntity) {
        //修改订单主表信息
        this.updateById(orderEntity);
        //修改桌台状态
        Table table = tableService.getById(orderEntity.getTableId());
        table.setStatus(SystemCode.TABLE_STATUS_FREE);
        tableService.updateById(table);
        return true;
    }

    @Reference(version = "1.0.0", check = false)
    private ICreditService creditService;
    @Reference(version = "1.0.0", check = false)
    private ICreditCompanyUserService creditCompanyUserService;
    @Reference(version = "1.0.0", check = false)
    private ICreditLogService creditLogService;

    @Override
    @Transactional
    @TenantAnnotation
    public boolean pay(OrderEntity orderEntity, CreditDTO creditDTO) {
        this.updateById(orderEntity);
        //设置挂账信息
        if (orderEntity.getPayType() == OrderPayType.CREDIT.getType()) {//如果是挂账的话
            String creditId = creditDTO.getCreditId();//拿到挂账id
            Credit credit = creditService.getById(creditId);//查询挂账实例

            //如果是个人用户
            if (credit.getCreditType() == SystemCode.CREDIT_TYPE_USER) {
                //判断挂账人信息是否正确
                if (!credit.getUserName().equals(creditDTO.getCreditUserName())) {
                    throw new BussinessException("挂账人信息不同，不允许挂账");
                }
                credit.setCreditAmount(credit.getCreditAmount() + creditDTO.getCreditAmount());//加上挂账金额
                creditService.saveOrUpdate(credit);//更新
            }
            //如果是公司用户
            List<CreditCompanyUser> companyUsers = null;
            if (credit.getCreditType() == SystemCode.CREDIT_TYPE_COMPANY) {
                List<CreditCompanyUser> companyUserList = creditCompanyUserService.getInfoList(creditId);//得到公司挂账人集合
                //判断当前挂账人在集合中是否存在
                Optional<CreditCompanyUser> resultInfo = companyUserList.stream()
                        .filter(user -> user.getUserName().equals(creditDTO.getCreditUserName())).findFirst();
                if (!resultInfo.isPresent()) {
                    //不存在，不允许挂账
                    throw new BussinessException("当前用户不在该公司中，请联系管家端进行设置");
                }
                companyUsers = companyUserList;
                credit.setCreditAmount(credit.getCreditAmount() + creditDTO.getCreditAmount());//加上挂账金额
                creditService.saveOrUpdate(credit);//更新
            }

            //挂账明细信息
            CreditLogs creditLogs = new CreditLogs();
            creditLogs.setCreditId(creditId);//挂账人id
            creditLogs.setOrderId(orderEntity.getOrderId());//订单id
            creditLogs.setType(credit.getCreditType());//挂账人类型
            creditLogs.setCreditAmount(creditDTO.getCreditAmount());//本次挂账金额
            creditLogs.setOrderAmount(orderEntity.getTotalAmount());//订单金额
            creditLogs.setReceivedAmount(orderEntity.getTotalAmount());//收款总金额
            creditLogs.setCreditAmount(creditDTO.getCreditAmount());//挂账总金额

            if (credit.getCreditType() == SystemCode.CREDIT_TYPE_COMPANY) {//挂账类型是公司
                creditLogs.setUserName(creditDTO.getCreditUserName());
                creditLogs.setCompanyName(credit.getCompanyName());
                Optional<CreditCompanyUser> optional = companyUsers.stream()
                        .filter(user -> user.getUserName().equals(creditDTO.getCreditUserName())).findFirst();
                String phone = optional.get().getPhone();
                creditLogs.setPhone(phone);
            } else if (credit.getCreditType() == SystemCode.CREDIT_TYPE_USER) {//挂账类型是个人
                creditLogs.setUserName(creditDTO.getCreditUserName());
                creditLogs.setPhone(credit.getPhone());
            }
            creditLogService.save(creditLogs);//保存挂账明细

            //修改桌台状态为空闲
            Table table = tableService.getById(orderEntity.getTableId());
            table.setStatus(SystemCode.TABLE_STATUS_FREE);
            tableService.updateById(table);
        }
        return true;
    }

    @Reference(version = "1.0.0", check = false)
    private ITableLogService tableLogService;
    /**
     * 换桌 原始和目标桌台的状态,桌台日志添加记录,更新订单桌台
     * @param orderId
     * @param targetTableId
     * @return
     */
    @Override
    @TenantAnnotation
    @Transactional
    public boolean changeTable(String orderId, String targetTableId) {

        String loginUserName = RpcContext.getContext().getAttachment("loginUserName");

        //得到目标桌台信息
        Table targetTable= tableService.getById(targetTableId);
        if(targetTable==null){
            throw new BussinessException("桌台不存在");
        }
        //查看目标桌台是否空闲
        if(targetTable.getStatus()!=SystemCode.TABLE_STATUS_FREE){
            throw new BussinessException("桌台非空闲状态，不能换桌");
        }
        //获取订单
        OrderEntity orderEntity = this.getById(orderId);
        //拿到原桌台信息
        Table sourceTable = tableService.getById(orderEntity.getTableId());
        //原来桌台设置为空闲
        sourceTable.setStatus(SystemCode.TABLE_STATUS_FREE);
        tableService.updateById(sourceTable);//更新原桌台
        //目标桌台设置为开桌状态
        targetTable.setStatus(SystemCode.TABLE_STATUS_OPEND);
        tableService.updateById(targetTable);//更新目标桌台

        //新增开桌日志
        TableLog tableLog =new TableLog();
        tableLog.setTableStatus(SystemCode.TABLE_STATUS_OPEND);
        tableLog.setCreateTime(LocalDateTime.now());
        tableLog.setTableId(targetTableId);
        tableLog.setUserNumbers(orderEntity.getPersonNumbers());
        tableLog.setUserId(loginUserName);//设置操作用户
        tableLogService.save(tableLog);

        //修改订单桌台关系,更新订单
        orderEntity.setTableId(targetTableId);
        return this.updateById(orderEntity);
    }
}
