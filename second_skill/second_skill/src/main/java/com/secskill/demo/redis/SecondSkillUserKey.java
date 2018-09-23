package com.secskill.demo.redis;

/**
 * @Description: User key
 * @author: zyh
 * @date: 2018-9-2
 */
public class SecondSkillUserKey extends BasePrefix {

    private static final int TOKEN_EXPIRE = 180;

    public SecondSkillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static final SecondSkillUserKey TOKEN = new SecondSkillUserKey(TOKEN_EXPIRE, "tk");
    public static final SecondSkillUserKey USER_ID = new SecondSkillUserKey(600, "id");

}
