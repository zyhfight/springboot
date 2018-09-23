package com.secskill.demo.service;

import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public interface SecondSkillUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    /**
     * 根据Id获取user
     * 优先从redis缓存中取
     * 缓存中不存在，从数据库中取
     * @param id
     * @return
     */
    SecondSkillUser getById(long id);

    /**
     * 用户登录
     * @param response
     * @param loginVo
     * @return
     */
    String login(HttpServletResponse response, LoginVo loginVo);

    /**
     * 根据token从redis获取user
     * 调用后会延长token有效期
     * @param response
     * @param token
     */
    SecondSkillUser getByToken(HttpServletResponse response, String token);

}
