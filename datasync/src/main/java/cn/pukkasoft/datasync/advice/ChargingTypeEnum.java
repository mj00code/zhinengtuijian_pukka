package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/6
 */
public enum ChargingTypeEnum {
    FIRST(1,"包年"),
    TWO(2,"包半年"),
    THREE(3,"包季"),
    FOUR(4,"包月"),
    FIVE(5,"按次"),
    DEFAULT(-9999,"");
    private Integer code;
    private String name;


    ChargingTypeEnum(Integer code, String name) {
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
        ChargingTypeEnum[] enumArr = ChargingTypeEnum.values();
        for (ChargingTypeEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static ChargingTypeEnum getByCode(String code) {
        ChargingTypeEnum[] enumArr = ChargingTypeEnum.values();
        for (ChargingTypeEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
