package com.secskill.demo.result;


/**
 * @Description: 返回码
 * @author: zyh
 * @date: 2018-9-2
 */
public enum CodeMsg {

    //通用模块
    SUCCESS(0,"success"),
    SERVER_ERROR(500100,"服务器异常"),
    BIND_EXCEPTION(500101,""),
    REQUEST_ILLEGAL(500102,"请求非法"),
    VERIFY_CODE_ILLEGAL(500102,"验证码输入错误"),
    ACCESS_LIMIT_REACHED(500103,"访问太频繁！"),

    //登录模块
    SESSION_ERROR(500210,"Session不存在或者已经失效"),
    PASSWORD_EMPTY(500211,"登录密码不能为空"),
    MOBILE_EMPTY(500212,"手机号不能为空"),
    MOBILE_ERROR(500213,"手机号格式错误"),
    MOBILE_NOT_EXIST(500214,"手机号码不存在"),
    PASSWORD_ERROR(500215,"密码错误"),


    //商品模块
    RESET_FAIL(500500,"重置库存失败！"),
    GOODS_NOT_EXIST(500501,"商品不存在"),

    //订单模块
    ORDER_NOT_EXIST(500400, "订单不存在"),
    DELETE_ORDER_FAIL(500401, "订单删除失败"),

    //秒杀模块
    SECOND_SKILL_OVER(500500,"商品已经秒杀完毕"),
    REPEATE_SECOND_SKILL(500501, "不能重复秒杀"),
    SECOND_SKILL_FAIL(500502, "秒杀失败");

    private int code;
    private String msg;

    CodeMsg(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args){
        String message = String.format(this.msg,args);
        BIND_EXCEPTION.setCode(this.code);
        BIND_EXCEPTION.setMsg(message);
        return BIND_EXCEPTION;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private void setCode(int code) {
        this.code = code;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }
}
