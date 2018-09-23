package com.secskill.demo.controller;

import com.secskill.demo.result.Result;
import com.secskill.demo.service.SecondSkillUserService;
import com.secskill.demo.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @Description: 登录
 * @author: zyh
 * @date: 2018-9-2
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SecondSkillUserService userService;

    @RequestMapping("/view")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/loginIn")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        logger.info("登录用户信息：{}" , loginVo);

        String token = userService.login(response,loginVo);
        return Result.success(token);

    }


}
