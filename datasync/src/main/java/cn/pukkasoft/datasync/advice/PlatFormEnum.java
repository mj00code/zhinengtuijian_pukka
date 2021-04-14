package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/3
 */
public enum  PlatFormEnum {
    CMCC("CMCC", "移动",1),
    CTCC("CTCC", "电信",3),
    CUCC("CUCC", "联通",2),
    DEFAULT("","",-9999);

    public String code;
    public String name;
    public Integer intEnumValue;
    PlatFormEnum(String code, String name,Integer intEnumValue) {
        this.code = code;
        this.name = name;
        this.intEnumValue=intEnumValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIntEnumValue(){
        return intEnumValue;
    }

    public void setIntEnumValue(Integer intEnumValue){
        this.intEnumValue=intEnumValue;
    }

    public static Map<String, String> getAll() {
        Map<String, String> retMap = new LinkedHashMap<>();
        PlatFormEnum[] enumArr = PlatFormEnum.values();
        for (PlatFormEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static PlatFormEnum getByCode(String code) {
        PlatFormEnum[] enumArr = PlatFormEnum.values();
        for (PlatFormEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}

