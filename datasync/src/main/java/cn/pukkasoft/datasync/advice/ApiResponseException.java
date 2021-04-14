package cn.pukkasoft.datasync.advice;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * author:magj
 * date:2020/11/2
 */
@Getter
public class ApiResponseException extends RuntimeException {
    /**异常code*/
    private Integer code;

    public ApiResponseException(ApiResponseEnum responseEnum){
        this(responseEnum,"");
    }

    public ApiResponseException(ApiResponseEnum responseEnum,String message){
        super(StringUtils.join(responseEnum.getMsg(),":",message));
        this.code = responseEnum.getCode();
    }


    public ApiResponseException(ApiResponseEnum responseEnum,String message,Throwable cause){
        super(MessageFormat.format(responseEnum.getMsg(),message),cause);
        this.code = responseEnum.getCode();
    }

    public ApiResponseException(Integer code,String message){
        super(message);
        this.code = code;
    }
}
