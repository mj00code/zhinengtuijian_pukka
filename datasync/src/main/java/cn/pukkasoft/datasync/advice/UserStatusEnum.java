package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/5
 */
public enum UserStatusEnum {
    FOA("FOA","联通正常"),
    FOJ("FOJ","联通主动停机"),
    FOK("FOK","联通欠费停机"),
    FOX("FOX","联通销户"),
    FIRST("1","移动预开户"),
    TWO("2","移动销户"),
    THREE("3","移动停机"),
    FOUR("4","移动复机"),
    FIVE("5","移动已激活"),
    SIX("6","移动增机"),
    SEVEN("7","移动撤单"),
    EIGHT("8","移动暂停"),
    DEFAULT("","");
    public String code;
    public String name;
    UserStatusEnum(String code, String name) {
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
        UserStatusEnum[] enumArr = UserStatusEnum.values();
        for (UserStatusEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static UserStatusEnum getByCode(String code) {
        UserStatusEnum[] enumArr = UserStatusEnum.values();
        for (UserStatusEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
