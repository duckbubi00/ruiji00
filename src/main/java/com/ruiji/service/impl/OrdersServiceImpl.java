package com.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.BaseContext;
import com.ruiji.common.CustomException;
import com.ruiji.entity.*;
import com.ruiji.mapper.OrdersMapper;
import com.ruiji.service.*;
import com.ruiji.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
       //获得用户id
        Long userId = BaseContext.getCurrentId();
        //查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> ShoppingCart = shoppingCartService.list(queryWrapper);
        if(ShoppingCart==null||ShoppingCart.size()==0){
            throw new CustomException("购物车不能为空");
        }
        //查询用户数据
        User user = userService.getById(userId);
        //查询用户地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        long orderId= IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger();
        List<OrderDetail>orderDetail = ShoppingCart.stream().map((item)-> {
            OrderDetail orderDetail1 = new OrderDetail();
           orderDetail1.setOrderId(orderId);
           orderDetail1.setNumber (item.getNumber());
           orderDetail1.setDishFlavor (item.getDishFlavor ());
           orderDetail1.setDishId (item.getDishId());
           orderDetail1.setSetmealId(item.getSetmealId());
           orderDetail1.setName (item.getName());
           orderDetail1.setImage (item.getImage ());
           orderDetail1.setAmount (item.getAmount()) ;
            amount.addAndGet(item.getAmount () .multiply(new BigDecimal(item.getNumber ()) ).intValue());
            return orderDetail1;
        }).collect(Collectors.toList()) ;

            //向订单表插入数据
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber (String.valueOf(orderId)) ;
        orders.setUserName (user.getName());
        orders.setConsignee(addressBook.getConsignee()) ;
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook. getProvinceName ())
                + (addressBook.getCityName() == null ?"" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ?"" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ?"" : addressBook.getDetail()));

        //向订单表插入一条数据
        this.save(orders);
        // 向订单明细表插入数据，多条
        orderDetailService.saveBatch(orderDetail);
        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
