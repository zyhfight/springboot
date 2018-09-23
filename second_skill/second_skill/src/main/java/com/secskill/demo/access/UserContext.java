package com.secskill.demo.access;

import com.secskill.demo.domain.SecondSkillUser;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public class UserContext {

    private static ThreadLocal<SecondSkillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SecondSkillUser user){
        userHolder.set(user);
    }

    public static SecondSkillUser getUser() {
        return userHolder.get();
    }
}
