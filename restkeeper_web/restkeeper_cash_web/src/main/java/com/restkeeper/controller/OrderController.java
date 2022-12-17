package com.restkeeper.controller;

import com.google.common.collect.Lists;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.entity.OrderDetailEntity;
import com.restkeeper.entity.OrderEntity;
import com.restkeeper.service.IOrderService;
import com.restkeeper.store.entity.DishCategory;
import com.restkeeper.store.service.IDishService;
import com.restkeeper.store.service.ISetMealService;
import com.restkeeper.tenant.TenantContext;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import com.restkeeper.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
}

