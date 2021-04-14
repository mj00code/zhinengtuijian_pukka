package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/5
 */
public enum EnablePlatForm {
    HW("HW","华为"),
    ZTE("ZTE","中兴"),
    DEFAULT("","");
    public String code;
    public String name;
    EnablePlatForm(String code, String name) {
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
        EnablePlatForm[] enumArr = EnablePlatForm.values();
        for (EnablePlatForm aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static EnablePlatForm getByCode(String code) {
        EnablePlatForm[] enumArr = EnablePlatForm.values();
        for (EnablePlatForm aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
