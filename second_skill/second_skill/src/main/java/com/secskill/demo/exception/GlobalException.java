package com.secskill.demo.exception;

import com.secskill.demo.result.CodeMsg;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public class GlobalException extends RuntimeException {
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(String.valueOf(codeMsg));
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
