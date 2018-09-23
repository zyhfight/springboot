package com.secskill.demo.result;

/**
 * @Description: 返回结果
 * @author: zyh
 * @date: 2018-9-2
 */
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg codeMsg){
        if(codeMsg == null){
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    /**
     * 成功返回时调用
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败返回时调用
     * @param codeMsg
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<>(codeMsg);
    }

    public  int getCode(){
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
