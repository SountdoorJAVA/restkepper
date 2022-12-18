package com.restkeeper.order.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.restkeeper.aop.TenantAnnotation;
import com.restkeeper.constants.OrderDetailType;
import com.restkeeper.constants.OrderPayType;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.dto.CreditDTO;
import com.restkeeper.dto.CurrentAmountCollectDTO;
import com.restkeeper.dto.CurrentHourCollectDTO;
import com.restkeeper.dto.DetailDTO;
import com.restkeeper.entity.OrderDetailEntity;
import com.restkeeper.entity.OrderDetailMealEntity;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.order.mapper.OrderMapper;
import com.restkeeper.service.IOrderDetailMealService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        //支付的时候 向t_order_detail_meal中存放 当前订单的套餐中的菜品明细
        saveOrderDetailMealInfo(orderEntity.getOrderId());

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
     *
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
        Table targetTable = tableService.getById(targetTableId);
        if (targetTable == null) {
            throw new BussinessException("桌台不存在");
        }
        //查看目标桌台是否空闲
        if (targetTable.getStatus() != SystemCode.TABLE_STATUS_FREE) {
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
        TableLog tableLog = new TableLog();
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

    //已付款总金额payTotal,已付款订单总数payTotalCount
    //未付款总金额noPayTotal,未付款订单总数noPayTotalCount
    //就餐总人数totalPerson,正在就餐的人数currentPerson
    @Override
    public CurrentAmountCollectDTO getCurrentCollect(LocalDate start, LocalDate endTime) {
        CurrentAmountCollectDTO result = new CurrentAmountCollectDTO();

        //查询并设置已付款总金额 - 订单表,已结账订单,合并付款金额
        QueryWrapper<OrderEntity> totalPayQueryWrapper = new QueryWrapper<>();
        totalPayQueryWrapper.select("sum(pay_amount) as total_amount")
                .lambda()
                .eq(OrderEntity::getPayStatus, SystemCode.ORDER_STATUS_PAYED)
                .ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, endTime);
        OrderEntity totalPayAmount = this.getOne(totalPayQueryWrapper);
        result.setPayTotal(totalPayAmount != null ? totalPayAmount.getTotalAmount() : 0);

        //查询并设置已付款总单数
        QueryWrapper<OrderEntity> totalPayCountWrapper = new QueryWrapper<>();
        totalPayCountWrapper.lambda()
                .ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, endTime)
                .eq(OrderEntity::getPayStatus, SystemCode.ORDER_STATUS_PAYED);
        int totalPayCount = this.count(totalPayCountWrapper);
        result.setPayTotalCount(totalPayCount);

        //查询并设置未付款总金额
        QueryWrapper<OrderEntity> noPayTotalQueryWrapper = new QueryWrapper<>();
        noPayTotalQueryWrapper.select("sum(total_amount) as total_amount")
                .lambda()
                .ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, endTime)
                .eq(OrderEntity::getPayStatus, SystemCode.ORDER_STATUS_NOTPAY);
        OrderEntity noPayTotalAmount = this.getOne(noPayTotalQueryWrapper);
        result.setNoPayTotal(noPayTotalAmount != null ? noPayTotalAmount.getTotalAmount() : 0);

        //查询并设置未付款总单数
        QueryWrapper<OrderEntity> noPayTotalCountWrapper = new QueryWrapper<>();
        noPayTotalCountWrapper.lambda()
                .ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, endTime)
                .eq(OrderEntity::getPayStatus, SystemCode.ORDER_STATUS_NOTPAY);
        int noPayTotalCount = this.count(noPayTotalCountWrapper);
        result.setNoPayTotalCount(noPayTotalCount);

        //查询并设置已结账就餐人数
        QueryWrapper<OrderEntity> payedTotalPersonQueryWrapper = new QueryWrapper<>();
        payedTotalPersonQueryWrapper.select("sum(person_numbers) as person_numbers")
                .lambda()
                .ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, endTime)
                .eq(OrderEntity::getPayStatus, SystemCode.ORDER_STATUS_PAYED);
        OrderEntity payedTotalPerson = this.getOne(payedTotalPersonQueryWrapper);
        result.setTotalPerson(payedTotalPerson != null ? payedTotalPerson.getPersonNumbers() : 0);

        //查询并设置未结账就餐人数
        QueryWrapper<OrderEntity> notPayTotalPersonQueryWrapper = new QueryWrapper<>();
        notPayTotalPersonQueryWrapper.select("sum(person_numbers) as person_numbers")
                .lambda().ge(OrderEntity::getLastUpdateTime, start)
                .lt(OrderEntity::getLastUpdateTime, endTime)
                .eq(OrderEntity::getPayStatus, SystemCode.ORDER_STATUS_NOTPAY);

        OrderEntity notPayTotalPerson = this.getOne(notPayTotalPersonQueryWrapper);
        result.setCurrentPerson(notPayTotalPerson != null ? notPayTotalPerson.getPersonNumbers() : 0);
        return result;
    }

    @Override
    public List<CurrentHourCollectDTO> getCurrentHourCollect(LocalDate start, LocalDate end, Integer type) {
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        if (type == 1) {
            //针对销售额求和,查询 支付金额,订单最后更新的小时, 查询条件 订单完成支付, 时间在start-end之间, 按小时聚合 ,按时间从小到大排序
            wrapper.select("sum(total_amount) as total_amount", "hour(last_update_time) as current_date_hour")
                    .lambda()
                    .ge(OrderEntity::getLastUpdateTime, start)
                    .lt(OrderEntity::getLastUpdateTime, end);
        }
        if (type == 2) {
            //针对销售量（单数）求和
            wrapper.select("count(total_amount) as total_amount", "hour(last_update_time) as current_date_hour")
                    .lambda()
                    .ge(OrderEntity::getLastUpdateTime, start)
                    .lt(OrderEntity::getLastUpdateTime, end);
        }
        //按current_date_hour分组和升序(从小到大)排序
        wrapper.groupBy("current_date_hour").orderByDesc("current_date_hour");
        //数据库查询结果为
        List<OrderEntity> list = this.getBaseMapper().selectList(wrapper);
        //创建一个返回结果的集合
        List<CurrentHourCollectDTO> result = new ArrayList<>();
        //将数据库查询结果保存到返回结果中
        list.forEach(o -> {
            CurrentHourCollectDTO collectDTO = new CurrentHourCollectDTO();
            collectDTO.setCurrentDateHour(o.getCurrentDateHour());
            collectDTO.setTotalAmount(o.getTotalAmount());
            result.add(collectDTO);
        });
        //结果集需要返回24个对象的结果,如果某些时段没有订单结算,则需要自动添加
        for (int i = 0; i < 23; i++) {
            int hour = i;
            //比较结果集,看有没有当前小时的记录,没有的话就创建并添加到结果集
            if (!result.stream().anyMatch(c -> c.getCurrentDateHour() == hour)) {
                CurrentHourCollectDTO collectDTO = new CurrentHourCollectDTO();
                collectDTO.setCurrentDateHour(i);
                collectDTO.setTotalAmount(0);
                result.add(collectDTO);
            }
        }
        //经过遍历,结果集中所需记录已补全,现在需要根据时间从小到大进行排序
        result.sort((a, b) -> Integer.compare(a.getCurrentDateHour(), b.getCurrentDateHour()));

        return result;
    }

    @Reference(version = "1.0.0", check = false)
    private ISetMealDishService setMealDishService;

    @Autowired
    @Qualifier("orderDetailMealService")
    private IOrderDetailMealService orderDetailMealService;

    //统计计算向t_order_detail_meal 订单套餐菜品明细表 中存放数据
    //1.获取订单中的套餐
    //2.再获取套餐下的菜品
    //3.将菜品信息转换成另一种格式,进行保存数据库
    @TenantAnnotation
    private void saveOrderDetailMealInfo(String orderId) {
        //获取订单明细表中的套餐信息
        QueryWrapper<OrderDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(OrderDetailEntity::getOrderId, orderId)
                .eq(OrderDetailEntity::getDishType, 2);
        List<OrderDetailEntity> orderDetailSetMealList = orderDetailService.list(wrapper);
        //遍历套餐 获取套餐下的菜品信息
        orderDetailSetMealList.forEach(orderSetMeal -> {
            //通过套餐id获取该套餐下的菜品信息
            List<Dish> dishList = setMealDishService.getAllDishBySetMealId(orderSetMeal.getDishId());
            //插入到order_detail_meal数据库中（套餐中的每一个菜品信息）
            OrderDetailMealEntity orderDetailMealEntity = new OrderDetailMealEntity();
            //复制公有信息
            BeanUtils.copyProperties(orderSetMeal, orderDetailMealEntity);
            //当前套餐的优惠比例 = 套餐支付金额 / 套餐内菜品总价
            float allDishPriceInsetMeal = dishList.stream()
                    .map(d -> d.getPrice() * setMealDishService.getDishCopiesInSetMeal(d.getId(), orderSetMeal.getDishId()))
                    .reduce(Integer::sum).get()
                    * orderSetMeal.getDishNumber();
            float rate = orderSetMeal.getDishAmount() / allDishPriceInsetMeal;
            //遍历套餐内菜品集合 循环补充其他信息
            dishList.forEach(d -> {
                //去除相关重复信息
                orderDetailMealEntity.setDetailId(null);//主键id置null,添加记录会自动生成
                orderDetailMealEntity.setShopId(null);//执行sql自动添加
                orderDetailMealEntity.setStoreId(null);

                orderDetailMealEntity.setDishId(d.getId());
                orderDetailMealEntity.setDishName(d.getName());
                orderDetailMealEntity.setDishPrice(d.getPrice());
                orderDetailMealEntity.setDishType(1);
                orderDetailMealEntity.setDishCategoryName(d.getDishCategory().getName());
                //获取套餐中该菜品数量 通过菜品id和套餐id获取
                Integer dishCopies = setMealDishService.getDishCopiesInSetMeal(d.getId(), orderSetMeal.getDishId());
                //菜品数量为 当前订单中套餐数量*套餐中该菜品数量
                orderDetailMealEntity.setDishNumber(orderSetMeal.getDishNumber() * dishCopies);
                //菜品总价
                orderDetailMealEntity.setDishAmount((int) (d.getPrice() * dishCopies * orderSetMeal.getDishNumber()));

                //保存一条数据
                orderDetailMealService.save(orderDetailMealEntity);
            });
        });

    }
}
