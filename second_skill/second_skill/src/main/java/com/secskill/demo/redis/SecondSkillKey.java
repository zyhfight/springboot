package com.secskill.demo.redis;

/**
 * @Description: 秒杀 key
 * @author: zyh
 * @date: 2018-9-2
 */
public class SecondSkillKey extends BasePrefix{

    private SecondSkillKey( int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static final SecondSkillKey IS_GOOD_OVER  = new SecondSkillKey(0, "go");
    public static final SecondSkillKey SECOND_SKILL_PATH = new SecondSkillKey(600, "mp");
    public static final SecondSkillKey SECOND_SKILL_VERIFY_CODE = new SecondSkillKey(600, "vc");
}
