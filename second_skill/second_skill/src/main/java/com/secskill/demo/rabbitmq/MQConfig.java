package com.secskill.demo.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: RabbitMQ Config
 * @author: zyh
 * @date: 2018-9-2
 */
@Configuration
public class MQConfig {

    public static final String SECOND_SKILL_QUEUE = "second.skill.queue";
    public static final String QUEUE = "queue";


    /**
     * Direct模式
     * */
    @Bean
    public Queue directQueue(){
        return new Queue(SECOND_SKILL_QUEUE,true);
    }

    /**
     * Topic模式
     * */

    /**
     * Fanout模式
     * */

    /**
     * Header模式
     * */
}
