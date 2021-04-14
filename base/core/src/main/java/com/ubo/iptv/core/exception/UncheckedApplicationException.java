package com.ubo.iptv.core.exception;

import com.ubo.iptv.core.enums.CodeEnum;
import org.springframework.core.NestedRuntimeException;

/**
 * @author huangjian
 */
@SuppressWarnings("serial")
public class UncheckedApplicationException extends NestedRuntimeException {

    /**
     * 异常信息
     */
    private CodeEnum codeEnum;

    public CodeEnum getCodeEnum() {
        return codeEnum;
    }

    /**
     * 数据现场，通常是前台发送过来的数据。由于目前采用了前后端分离方案，表单失败后数据不需要由后台回传回填，可以忽略此字段
     */
    private Object data;

    /**
     *
     * @param codeEnum
     * @param args for formatting msg
     */
    public UncheckedApplicationException(CodeEnum codeEnum, Object... args){
        super(codeEnum.msg(args));
        this.codeEnum = codeEnum;
    }

    /**
     * 为异常信息附加其他数据,扩展方法
     */
    public UncheckedApplicationException withData(Object data){
        this.data = data;
        return this;
    }
}
