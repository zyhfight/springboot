package com.secskill.demo.exception;

import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.result.Result;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 统一异常处理器
 * @author: zyh
 * @date: 2018-9-2
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class) //缺少这个注解，统一异常处理器失效，ajax请求永远是error
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        logger.error(ExceptionUtils.getStackTrace(e));
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        }else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            ObjectError error = ex.getAllErrors().get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_EXCEPTION.fillArgs(msg));
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }

}
