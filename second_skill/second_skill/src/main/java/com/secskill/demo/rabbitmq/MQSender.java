package com.secskill.demo.rabbitmq;

import com.secskill.demo.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendSecondSkillMessage(SecondSkillMessage message){
        String msg = RedisService.beanToString(message);
        log.info("send message: {}",msg);
        amqpTemplate.convertAndSend(MQConfig.SECOND_SKILL_QUEUE,msg);
    }

}
