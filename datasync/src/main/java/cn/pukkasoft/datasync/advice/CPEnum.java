package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/6
 */
public enum CPEnum {
    ZERO(0,"家开"),
    FIRST(1,"咪咕"),
    DEFAULT(-9999,"");

    private Integer code;
    private String name;


    CPEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static Map<Integer, String> getAll() {
        Map<Integer, String> retMap = new LinkedHashMap<>();
        CPEnum[] enumArr = CPEnum.values();
        for (CPEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static CPEnum getByCode(String code) {
        CPEnum[] enumArr = CPEnum.values();
        for (CPEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
