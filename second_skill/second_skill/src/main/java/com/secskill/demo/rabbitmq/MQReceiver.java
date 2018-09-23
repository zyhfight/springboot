package com.secskill.demo.rabbitmq;

import com.secskill.demo.domain.SecondSkillOrder;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.service.GoodsService;
import com.secskill.demo.service.OrderService;
import com.secskill.demo.service.SecondSkillService;
import com.secskill.demo.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class MQReceiver {
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SecondSkillService secondSkillService;

    @RabbitListener(queues = MQConfig.SECOND_SKILL_QUEUE)
    public void receive(String message){
        log.info("receive message: {}", message);
        SecondSkillMessage skillMessage = RedisService.stringToBean(message, SecondSkillMessage.class);
        SecondSkillUser user = skillMessage.getUser();
        long goodsId = skillMessage.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goodsVo.getStockCount();
        if(stockCount <= 0){
            return;
        }

        SecondSkillOrder order = orderService.getSecondSkillOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if(order != null){
            return;
        }
        secondSkillService.secondSkill(user,goodsVo);
    }


}
