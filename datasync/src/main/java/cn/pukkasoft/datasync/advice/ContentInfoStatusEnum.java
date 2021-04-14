package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/5
 */
public enum ContentInfoStatusEnum {
    ZERO(0,"下线"),
    FIRST(1,"上线"),
    DEFAULT(-9999,"");
   // 0:预订购,1:已订购,2:待支付,3:支付失败,4:已退订,5:续订购成功,6:续订购失败
    private Integer code;
    private String name;


    ContentInfoStatusEnum(Integer code, String name) {
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
        ContentInfoStatusEnum[] enumArr = ContentInfoStatusEnum.values();
        for (ContentInfoStatusEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static ContentInfoStatusEnum getByCode(String code) {
        ContentInfoStatusEnum[] enumArr = ContentInfoStatusEnum.values();
        for (ContentInfoStatusEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
