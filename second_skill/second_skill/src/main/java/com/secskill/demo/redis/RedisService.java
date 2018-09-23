package com.secskill.demo.redis;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class RedisService {

    private static  final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String jedisStr = jedis.get(realKey);
            T t = stringToBean(jedisStr,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象到redis
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if(StringUtils.isEmpty(str)){
                return false;
            }

            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if(seconds <= 0){
                jedis.set(realKey,str);//永不失效
            }else {
                jedis.setex(realKey,seconds,str);//有效期为seconds秒
            }
            return true;
        }finally {
            returnToPool(jedis);
        }

    }


    /**
     * 判断key是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realkey = prefix.getPrefix() + key;
            return jedis.exists(realkey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除key
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            return  jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            return  jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除指定的KeyPrefix
     * @param prefix
     * @return
     */
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(CollectionUtils.isEmpty(keys)) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch ( Exception e) {
            logger.error("删除{}，Exception：{}",prefix.getPrefix(), ExceptionUtils.getStackTrace(e));
            return false;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 遍历指定key
     * @param key
     * @return
     */
    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<>();
            String cursor = "0";
            ScanParams scanParams = new ScanParams();
            scanParams.match("*" + key + "*");
            scanParams.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, scanParams);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * Bean 转 String
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value){
        if(value == null){
            return null;
        }

        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return ""+value;
        }else if(clazz == String.class) {
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class) {
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * string 转 Bean
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }

        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return  (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }

    }


    private void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }

}
