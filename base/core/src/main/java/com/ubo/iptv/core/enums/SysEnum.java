package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

public enum SysEnum implements CommonEnum {
    @CommonEntity(value = "CM", description = "移动")
    CMCC,
    @CommonEntity(value = "CU", description = "联通")
    CUCC,
    @CommonEntity(value = "CTC", description = "电信")
    CTCC;

    /**
     * 根据value获取description
     *
     * @param value
     * @return
     */
    public static String description(Object value) {
        if (value != null) {
            for (SysEnum sysEnum : SysEnum.values()) {
                if (sysEnum.value().equals(value.toString())) {
                    return sysEnum.description();
                }
            }
            return value.toString();
        }
        return null;
    }

    /**
     * 根据spId获取运营商
     *
     * @param spId
     * @return
     */
    public static String getSysIdBySpId(Integer spId) {
        if (spId != null) {
            switch (spId) {
                case 1:
                    return CMCC.value();
                case 2:
                    return CUCC.value();
                case 3:
                    return CTCC.value();
            }
        }
        return null;
    }

    /**
     * 根据value获取SPID
     *
     * @param sysId
     * @return
     */
    public static Integer getSpIdBySysId(String sysId) {
        if (sysId != null) {
            switch (sysId) {
                case "m":
                    return 1005;
                case "u":
                    return 1009;
            }
        }
        return null;
    }

    /**
     * 根据platform获取运营商
     *
     * @param platform
     * @return
     */
    public static String getSysIdByPlatform(Integer platform) {
        if (platform != null) {
            switch (platform) {
                case 1:
                    return CMCC.value();
                case 2:
                    return CUCC.value();
            }
        }
        return null;
    }

    /**
     * 根据value获取Platform
     *
     * @param sysId
     * @return
     */
    public static Integer getPlatformBySysId(String sysId) {
        if (sysId != null) {
            switch (sysId) {
                case "m":
                    return 1;
                case "u":
                    return 2;
            }
        }
        return null;
    }
}
