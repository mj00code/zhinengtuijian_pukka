package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/3
 */
public enum  AreaCodeEnum {

    LINXIA("930","临夏市"),
    LANZHOU("931","兰州"),
    DINGXI("932","定西市"),
    PINGLIANG("933","平凉市"),
    QINYAN("934","庆阳市"),
    WUWEI("935","武威市"),
    ZHANGYE("936","张掖市"),
    JIUQUAN("937","酒泉市"),
    TIANSHUI("938","天水市"),
    LONGNAN("939","陇南市"),
    GANNAN("941","甘南市"),
    BAIYIN("943","白银市"),
    JINCHANG("945","金昌市"),
    JIAYUGUAN("947","嘉峪关"),
    DEFAULT("","");
    public String code;
    public String name;
    AreaCodeEnum(String code, String name) {
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
        AreaCodeEnum[] enumArr = AreaCodeEnum.values();
        for (AreaCodeEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static AreaCodeEnum getByCode(String code) {
        AreaCodeEnum[] enumArr = AreaCodeEnum.values();
        for (AreaCodeEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
