package com.secskill.demo.service;

import com.secskill.demo.domain.OrderInfo;
import com.secskill.demo.domain.SecondSkillOrder;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public interface OrderService {

    /**
     * 通过user id 和 商品id获取秒杀订单
     * 从redis缓存中获取
     * @param userId
     * @param goodsId
     * @return
     */
    SecondSkillOrder getSecondSkillOrderByUserIdAndGoodsId(long userId, long goodsId);

    /**
     * 根据订单id获取订单
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfoById(long orderId);

    /**
     * 根据user和商品vo创建订单
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional(rollbackFor = GlobalException.class)
    OrderInfo createOrder(SecondSkillUser user, GoodsVo goodsVo);

    /**
     * 删除所有订单信息
     */
    @Transactional(rollbackFor = GlobalException.class)
    void deleteOrders();


}
