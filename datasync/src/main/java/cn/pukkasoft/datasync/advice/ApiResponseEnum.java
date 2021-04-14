package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/2
 */
public enum ApiResponseEnum {
    SERVER_INTERNAL_ERROR(9999, "服务器内部错误"),
    ERROR_PARAMETER(3008, "参数错误"),
    SUCCESS(0, "成功"),
    ERROR(-1, "失败"),
    DEFAULT(-9999,"");
    public Integer code;
    public String msg;
    ApiResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public static Map<Integer, String> getAll() {
        Map<Integer, String> retMap = new LinkedHashMap<>();
        ApiResponseEnum[] enumArr = ApiResponseEnum.values();
        for (ApiResponseEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getMsg());
        }
        return retMap;
    }

    public static ApiResponseEnum getByCode(String code) {
        ApiResponseEnum[] enumArr = ApiResponseEnum.values();
        for (ApiResponseEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
