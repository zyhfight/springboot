package com.secskill.demo.service.impl;

import com.secskill.demo.dao.OrderDao;
import com.secskill.demo.domain.OrderInfo;
import com.secskill.demo.domain.SecondSkillOrder;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.redis.OrderKey;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.service.OrderService;
import com.secskill.demo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    @Override
    public SecondSkillOrder getSecondSkillOrderByUserIdAndGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.SECOND_SKILL_ORDER,""+userId+"_"+goodsId,SecondSkillOrder.class);
    }

    @Override
    public OrderInfo getOrderInfoById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public OrderInfo createOrder(SecondSkillUser user, GoodsVo goodsVo) {

        OrderInfo orderInfo = initOrderInfo(user,goodsVo);
        long orderCnt = orderDao.insert(orderInfo);
        if(orderCnt <= 0){
            throw new GlobalException(CodeMsg.SECOND_SKILL_FAIL);
        }

        SecondSkillOrder secondSkillOrder = initSecondSkillOrder(orderInfo,user,goodsVo);
        int secondCnt = orderDao.insertSecondSkillOrder(secondSkillOrder);
        if(secondCnt <= 0){
            throw new GlobalException(CodeMsg.SECOND_SKILL_FAIL);
        }

        //订单缓存
        redisService.set(OrderKey.SECOND_SKILL_ORDER,""+user.getId()+"_"+goodsVo.getId(),secondSkillOrder);

        return orderInfo;
    }

    @Override
    public void deleteOrders() {
        boolean delOrdersSuc = orderDao.deleteOrders();
        if(!delOrdersSuc){
            throw new GlobalException(CodeMsg.DELETE_ORDER_FAIL);
        }
        boolean deletSecondSkillOrderSuc = orderDao.deleteSecondSkillOrders();
        if(!deletSecondSkillOrderSuc){
            throw new GlobalException(CodeMsg.DELETE_ORDER_FAIL);
        }
    }

    private OrderInfo initOrderInfo(SecondSkillUser user, GoodsVo goods){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSecSkillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        return orderInfo;
    }

    private SecondSkillOrder initSecondSkillOrder(OrderInfo orderInfo, SecondSkillUser user, GoodsVo goods){
        SecondSkillOrder secondSkillOrder = new SecondSkillOrder();
        secondSkillOrder.setGoodsId(goods.getId());
        secondSkillOrder.setOrderId(orderInfo.getId());
        secondSkillOrder.setUserId(user.getId());
        return secondSkillOrder;
    }
}
