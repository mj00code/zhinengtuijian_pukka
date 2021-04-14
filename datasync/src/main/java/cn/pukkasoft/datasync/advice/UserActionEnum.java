package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/6
 */
public enum UserActionEnum {

    INJECTION("INJECTION","用户同步"),
    UPDATE_STATUS("UPDATE_STATUS","用户状态变更"),
    DEFAULT("","");

    public String code;
    public String name;

    UserActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
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


    public static Map<String, String> getAll() {
        Map<String, String> retMap = new LinkedHashMap<>();
        UserActionEnum[] enumArr = UserActionEnum.values();
        for (UserActionEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static UserActionEnum getByCode(String code) {
        UserActionEnum[] enumArr = UserActionEnum.values();
        for (UserActionEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
