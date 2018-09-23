package com.secskill.demo.controller;

import com.secskill.demo.access.AccessLimit;
import com.secskill.demo.domain.OrderInfo;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.redis.GoodsKey;
import com.secskill.demo.redis.OrderKey;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.result.Result;
import com.secskill.demo.service.GoodsService;
import com.secskill.demo.service.OrderService;
import com.secskill.demo.service.SecondSkillService;
import com.secskill.demo.vo.GoodsVo;
import com.secskill.demo.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    SecondSkillService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<OrderDetailVo> info(Model model, SecondSkillUser user,
                                      @RequestParam("orderId") long orderId) {

        OrderInfo order = getOrderInfo(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        GoodsVo goodsVo  = getGoodsVo(order);
        if (goodsVo == null) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goodsVo);
        return Result.success(vo);

    }

    private OrderInfo getOrderInfo(long orderId){
        OrderInfo order = redisService.get(OrderKey.SECOND_SKILL_ORDER, ""+orderId, OrderInfo.class);
        if(order == null){
            order = orderService.getOrderInfoById(orderId);
            redisService.set(OrderKey.SECOND_SKILL_ORDER, ""+orderId, order);
        }
        return order;
    }

    private GoodsVo getGoodsVo(OrderInfo order){
        long goodsId = order.getGoodsId();
        GoodsVo goodsVo = redisService.get(GoodsKey.GOOD_DETAIL, ""+goodsId, GoodsVo.class);
        if(goodsVo == null){
            goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
            redisService.set(GoodsKey.GOOD_DETAIL, ""+goodsId, goodsVo);
        }
        return goodsVo;
    }


}
