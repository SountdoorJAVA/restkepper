package com.restkeeper.controller;

import com.google.common.collect.Lists;
import com.restkeeper.constants.OrderPayType;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.dto.CreditDTO;
import com.restkeeper.dto.DetailDTO;
import com.restkeeper.entity.OrderDetailEntity;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.entity.ReverseOrder;
import com.restkeeper.service.IOrderService;
import com.restkeeper.service.IReverseOrderService;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishCategory;
import com.restkeeper.store.service.IDishService;
import com.restkeeper.store.service.ISetMealService;
import com.restkeeper.tenant.TenantContext;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import com.restkeeper.utils.SequenceUtils;
import com.restkeeper.vo.OrderDetailVO;
import com.restkeeper.vo.OrderVO;
import com.restkeeper.vo.PayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MORRIS --> Java
 * @date 2022-12-17 19:14:43
 */
@Api(tags = {"订单接口"})
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(version = "1.0.0", check = false)
    private IOrderService orderService;
    @Reference(version = "1.0.0", check = false)
    private IDishService dishService;
    @Reference(version = "1.0.0", check = false)
    private ISetMealService setMealService;
    @Reference(version = "1.0.0", check = false)
    private IReverseOrderService reverseOrderService;

    @ApiOperation("下单")
    @PostMapping("/add")
    public Result addOrder(@RequestBody OrderVO orderVO) {
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setTableId(orderVO.getTableId());
        orderEntity.setPayStatus(SystemCode.ORDER_STATUS_NOTPAY);
        orderEntity.setOperatorName(TenantContext.getLoginUserName());
        orderEntity.setOrderSource(SystemCode.ORDER_SOURCE_STORE);
        orderEntity.setTotalAmount(orderVO.getTotalAmount());
        orderEntity.setPersonNumbers(orderVO.getPersonNumbers());
        orderEntity.setOrderRemark(orderVO.getOrderRemark());
        orderEntity.setCreateTime(LocalDateTime.now());


        List<OrderDetailEntity> orderDetails = Lists.newArrayList();

        orderVO.getDishes().forEach(dishVO -> {

            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();

            orderDetailEntity.setDishName(dishVO.getDishName());
            orderDetailEntity.setDishType(dishVO.getType());
            orderDetailEntity.setDishId(dishVO.getDishId());
            orderDetailEntity.setDishAmount(dishVO.getDishNumber() * dishVO.getPrice());
            orderDetailEntity.setDishNumber(dishVO.getDishNumber());
            orderDetailEntity.setFlavorRemark(dishVO.getFlavorList().toString());
            orderDetailEntity.setTableId(orderVO.getTableId());
            orderDetailEntity.setDishPrice(dishVO.getPrice());
            DishCategory dishCategory = null;
            if (orderDetailEntity.getDishType() == 1) {
                dishCategory = dishService.getById(dishVO.getDishId()).getDishCategory();
            } else {
                dishCategory = setMealService.getById(dishVO.getDishId()).getCategory();
            }

            if (dishCategory != null) {
                orderDetailEntity.setDishCategoryName(dishCategory.getName());
            }
            orderDetails.add(orderDetailEntity);
        });
        orderEntity.setOrderDetails(orderDetails);

        //请求第一次 调用注册到dubbo的orderService,先经过dubbo消费者拦截器
        String orderId = orderService.addOrder(orderEntity);

        Result result = new Result();
        result.setStatus(ResultCode.success);
        result.setData(orderId);
        return result;
    }

    @ApiOperation("加菜")
    @PostMapping("/plusDish/orderId/{orderId}")
    public Result orderPlusDish(@PathVariable String orderId, @RequestBody List<OrderDetailVO> details) {
        //获取当前订单
        OrderEntity orderEntity = orderService.getById(orderId);
        //创建订单明细集合
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

        int amount = 0;

        //遍历加菜菜品集合
        for (OrderDetailVO detail : details) {
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();

            orderDetailEntity.setOrderId(orderEntity.getOrderId());
            orderDetailEntity.setOrderNumber(SequenceUtils.getSequenceWithPrefix(orderEntity.getOrderNumber()));
            orderDetailEntity.setTableId(orderEntity.getTableId());
            orderDetailEntity.setDetailStatus(detail.getStatus());
            orderDetailEntity.setAddRemark(detail.getDishRemark());
            orderDetailEntity.setDishId(detail.getDishId());
            orderDetailEntity.setDishType(detail.getType());
            orderDetailEntity.setDishName(detail.getDishName());
            orderDetailEntity.setDishPrice(detail.getPrice());
            orderDetailEntity.setDishNumber(detail.getDishNumber());
            orderDetailEntity.setDishAmount(detail.getDishNumber() * detail.getPrice());
            orderDetailEntity.setDishRemark(detail.getDishRemark());
            orderDetailEntity.setFlavorRemark(detail.getFlavorList().toString());

            Dish dish = dishService.getById(detail.getDishId());
            if (dish != null) {
                orderDetailEntity.setDishCategoryName(dish.getDishCategory().getName());
            }

            //当前加菜总金额
            amount += orderDetailEntity.getDishAmount();

            orderDetailEntities.add(orderDetailEntity);
        }

        orderEntity.setOrderDetails(orderDetailEntities);
        orderEntity.setTotalAmount(orderEntity.getTotalAmount() + amount);

        Result result = new Result();
        result.setStatus(ResultCode.success);
        orderId = orderService.addOrder(orderEntity);
        result.setData(orderId);
        return result;
    }

    @ApiOperation("退菜")
    @PostMapping("/returnDish/{detailId}")
    public boolean returnDish(@PathVariable String detailId, @RequestBody List<String> remarks) {
        DetailDTO detailDTO = new DetailDTO();
        detailDTO.setRemarks(remarks);
        detailDTO.setDetailId(detailId);
        return orderService.returnDish(detailDTO);
    }

    @ApiOperation("结账")
    @PostMapping("/pay/orderId/{orderId}")
    public boolean pay(@PathVariable String orderId, @RequestBody PayVO payVO) {
        OrderEntity orderEntity = orderService.getById(orderId);
        orderEntity.setPayAmount(payVO.getPayAmount());
        orderEntity.setSmallAmount(payVO.getSmallAmount());
        orderEntity.setPayStatus(SystemCode.ORDER_STATUS_PAYED);
        orderEntity.setPayType(payVO.getPayType());

        //如果挂账
        if (payVO.getPayType() == OrderPayType.CREDIT.getType()) {
            CreditDTO creditDTO = new CreditDTO();
            creditDTO.setCreditId(payVO.getCreditId());
            creditDTO.setCreditAmount(payVO.getCreditAmount());
            creditDTO.setCreditUserName(payVO.getCreditUserName());
            return orderService.pay(orderEntity, creditDTO);
        }
        return orderService.pay(orderEntity);
    }

    @ApiOperation("反结账")
    @PostMapping("/reverse/{orderId}")
    public boolean reverse(@PathVariable String orderId, @RequestBody List<String> remarks) {
        ReverseOrder reverseOrder = new ReverseOrder();
        reverseOrder.setOrderId(orderId);
        reverseOrder.setRemark(remarks.toString());
        return reverseOrderService.reverse(reverseOrder);
    }
}

