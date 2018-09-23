package com.secskill.demo.redis;

import com.secskill.demo.SecondSkillApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public class RedisConfigtest extends SecondSkillApplicationTests {

    @Autowired
    RedisConfig redisConfig;

    @Test
    public void testRedisConfig(){
        System.out.println(redisConfig.getHost());
    }
}
