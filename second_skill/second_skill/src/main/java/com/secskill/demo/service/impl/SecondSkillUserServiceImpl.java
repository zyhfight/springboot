package com.secskill.demo.service.impl;

import com.secskill.demo.dao.SecondSkillUserDao;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.redis.SecondSkillKey;
import com.secskill.demo.redis.SecondSkillUserKey;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.service.SecondSkillUserService;
import com.secskill.demo.util.MD5Util;
import com.secskill.demo.util.UUIDUtil;
import com.secskill.demo.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class SecondSkillUserServiceImpl implements SecondSkillUserService {

    private static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private SecondSkillUserDao userDao;

    @Autowired
    RedisService redisService;

    @Override
    public SecondSkillUser getById(long id) {

        SecondSkillUser user = redisService.get(SecondSkillUserKey.USER_ID, ""+id, SecondSkillUser.class);
        if(user != null){
            return user;
        }
        user = userDao.getById(id);
        if(user != null){
            redisService.set(SecondSkillUserKey.USER_ID,""+id, user);
        }
        return user;
    }

    @Override
    public SecondSkillUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }

        SecondSkillUser user = redisService.get(SecondSkillUserKey.TOKEN,token,SecondSkillUser.class);
        if(user != null){
            //延长有效期
            addCookie(response,token,user);
        }
        return user;
    }

    @Override
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        SecondSkillUser user = getById(Long.parseLong(mobile));
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPass2DBPass(formPass,saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return token;

    }

    private void addCookie(HttpServletResponse response, String token, SecondSkillUser user) {

        //分布式session
        redisService.set(SecondSkillUserKey.TOKEN,token,user);

        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        //设置cookie三分钟有效期
        cookie.setMaxAge(180);
        //在webapp文件夹下的所有应用共享cookie
        cookie.setPath("/");
        response.addCookie(cookie);

    }


}
