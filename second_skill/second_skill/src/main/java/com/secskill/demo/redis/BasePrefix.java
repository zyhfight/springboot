package com.secskill.demo.redis;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;
    private String prefix;

    /**
     * 永不过期
     * @param prefix
     */
    public BasePrefix(String prefix){
        this(0,prefix);
    }

    /**
     * @param expireSeconds:过期时间
     * @param prefix：key
     */
    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }


    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return getClass().getName() + ":" + prefix;
    }

}
