package com.secskill.demo.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 手机号码校验类
 * @author: zyh
 * @date: 2018-9-2
 */
public class ValidatorUitl {

    private static final Pattern mobilePattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String value) {
        if(StringUtils.isEmpty(value)){
            return false;
        }
        Matcher matcher = mobilePattern.matcher(value);
        return matcher.matches();
    }
}
