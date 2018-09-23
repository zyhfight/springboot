package com.secskill.demo.access;


import com.alibaba.fastjson.JSON;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.redis.AccessKey;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.result.Result;
import com.secskill.demo.service.SecondSkillService;
import com.secskill.demo.service.SecondSkillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    SecondSkillUserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (handler instanceof HandlerMethod) {
            SecondSkillUser user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if(user == null){
                  //  response.sendRedirect(request.getContextPath() + "/login/reLogin");
                    //request.getRequestDispatcher( request.getContextPath() + "/login/view").forward(request, response);
                    render(response, CodeMsg.REQUEST_ILLEGAL);
                    return false;
                }
                key += "_"  + user.getId();
            }

            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if(count == null){
                redisService.set(ak, key, 1);
            }else if (count < maxCount){
                redisService.incr(ak, key);
            }else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }

        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        try(
                OutputStream out = response.getOutputStream();
                )
        {
            String str = JSON.toJSONString(Result.error(codeMsg));
            out.write(str.getBytes("UTF-8"));
            out.flush();
        }
    }


    private SecondSkillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(SecondSkillUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SecondSkillUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for (Cookie cookie : cookies){
            if(cookieName.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
