package com.secskill.demo.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public class MD5Util {

    public static String  md5(String value){
        return DigestUtils.md5Hex(String.valueOf(value));
    }

    /**
     *
     * @param formPass：前端MD5后的password
     * @param saltDB：数据库salt
     * @return
     */
    public static String formPass2DBPass(String formPass,String saltDB){
        String str = "" + saltDB + formPass;
        return md5(str);
    }

}
