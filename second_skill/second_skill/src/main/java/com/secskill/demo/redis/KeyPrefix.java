package com.secskill.demo.redis;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public interface KeyPrefix {

    int expireSeconds();
    String getPrefix();

}
