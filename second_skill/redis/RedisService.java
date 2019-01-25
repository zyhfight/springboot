package com.cmbchina.sjdj.redis.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

/**
 * @Description: redis操作类
 * @author: 
 * @date: 2019-1-13 14:55
 */
@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    JedisCluster jedisCluster;

    /**
     * 获取单个对象
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){

            String realKey = prefix.getPrefix() + key;
            String jedisStr = jedisCluster.get(realKey);
            T t = stringToBean(jedisStr,clazz);  
            return t;
    }
    
    
    public <T> T getArray(KeyPrefix prefix,String key,Class<T> clazz){

        String realKey = prefix.getPrefix() + key;
        String jedisStr = jedisCluster.get(realKey);
        T t = (T) JSON.parseArray(jedisStr, clazz);
        return t;
}

    /**
     * 设置单个对象到redis
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix,String key,T value){
            String str = beanToString(value);
            if(StringUtils.isEmpty(str)){
                return false;
            }

            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            //永不失效
            if(seconds <= 0){
                jedisCluster.set(realKey,str);
            }else {
                //有效期为seconds秒
                jedisCluster.setex(realKey,seconds,str);
            }
            return true;
    }


    /**
     * 判断key是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix, String key){

            String realkey = prefix.getPrefix() + key;
            return jedisCluster.exists(realkey);

    }

    /**
     * 删除key
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix, String key){
            String realKey = prefix.getPrefix() + key;
            long ret = jedisCluster.del(realKey);
            return ret > 0;
    }

    /**
     * 增加值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            return  jedisCluster.incr(realKey);

    }

    /**
     * 减少值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey  = prefix.getPrefix() + key;
        return  jedisCluster.decr(realKey);
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
        List<String> keys = getKeys(prefix.getPrefix());
        if(CollectionUtils.isEmpty(keys)) {
            return true;
        }
        try{
            jedisCluster.del(keys.toArray(new String[0]));
            return true;
        }catch (Exception e) {
            logger.error("redis删除KeyPrefix： {}，异常： {}", prefix, ExceptionUtils.getStackTrace(e));
        }

        return false;

    }


    /**
     * 获取所有的节点，分别扫描每个节点，根据pattern获取节点中的key，然后整合。
     * @param prefixkey
     * @return
     */
    public List<String> getKeys(String prefixkey) {

        List<String> allKeyList = new ArrayList<>();

        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis jedis = entry.getValue().getResource();
            // 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
            if (!jedis.info("replication").contains("role:slave")) {
                Set<String> keys = jedis.keys(prefixkey + "*");
                if (keys.size() > 0) {
                    Map<Integer, List<String>> map = new HashMap<>();
                    for (String key : keys) {
                        // cluster模式执行多key操作的时候，这些key必须在同一个slot上，不然会报:JedisDataException:
                        // CROSSSLOT Keys in request don't hash to the same slot
                        int slot = JedisClusterCRC16.getSlot(key);
                        // 按slot将key分组，相同slot的key一起提交
                        if (map.containsKey(slot)) {
                            map.get(slot).add(key);
                        } else {
                            map.put(slot, Lists.newArrayList(key));
                        }
                    }
                    for (Map.Entry<Integer, List<String>> integerListEntry : map.entrySet()) {
                        List<String> keyList = integerListEntry.getValue();
                        allKeyList.addAll(keyList);
                    }
                }
            }
        }

        logger.info("{} 前缀下的所有key值：{}", prefixkey, allKeyList);

        return allKeyList;
    }

    /**
     * 遍历指定key（redis集群环境不能正常使用！）
     * 集群环境报错，参考csdn是有scan方法，获取keys为空
     * https://blog.csdn.net/qq_33999844/article/details/81381607
     * @param key
     * @return
     */
    @Deprecated
    public List<String> scanKeys(String key) {

            List<String> keys = new ArrayList<>();
            String cursor = "0";
            ScanParams scanParams = new ScanParams();
            scanParams.match("*" + key + "*");
//            scanParams.match("{"+key+"}");
            scanParams.count(100);
            do{
                ScanResult<String> ret = jedisCluster.scan(cursor, scanParams);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
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

}
