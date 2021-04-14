package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CodeEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 ** @author huangjian
 * @date 2017/4/21
 */
public interface CodeEnum {

    default CodeEntity codeEntity(){
        try {
            return this.getClass().getField(this.toString()).getAnnotation(CodeEntity.class);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    default int status(){
        return codeEntity().status();
    }

    default int result(){
        return codeEntity().result();
    }

    default String msg(Object... args){
        return String.format(codeEntity().msg(), args);
    }

    default String msg_client(Object... args){
        CodeEntity codeEntity = codeEntity();
        return String.format(StringUtils.isBlank(codeEntity.msg_client()) ? codeEntity.msg() : codeEntity.msg_client(), args);
    }

    /**
     * 返回带状态码的异常信息
     */
    default String fullMsg(Object... args){
        return String.format("code:[%d] msg:[%s]", status(), msg(args));
    }

    /**
     * toJsonStr
     * @param codeEnum
     * @return
     */
    static String toJsonStr(Class<? extends CodeEnum> codeEnum){
        StringBuffer buffer = new StringBuffer();
        Arrays.asList(codeEnum.getEnumConstants()).forEach((every)->{
            CodeEntity entity = every.codeEntity();
            buffer.append(entity.status()).append(":").append(entity.msg()).append(";\r\n");
        });
        return buffer.toString();
    }
}
