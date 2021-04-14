package cn.pukkasoft.datasync.advice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author:magj
 * date:2020/11/5
 */
public enum OrderStatusEnum{
    ZERO("0","预订购"),
    FIRST("1","已订购"),
    TWO("2","待支付"),
    THREE("3","支付失败"),
    FOUR("4","已退订"),
    FIVE("5","续订购成功"),
    SIX("6","续订购失败"),
    DEFAULT("","");
   // 0:预订购,1:已订购,2:待支付,3:支付失败,4:已退订,5:续订购成功,6:续订购失败
    private String code;
    private String name;


    OrderStatusEnum(String code, String name) {
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
        OrderStatusEnum[] enumArr = OrderStatusEnum.values();
        for (OrderStatusEnum aEnum : enumArr) {
            retMap.put(aEnum.getCode(), aEnum.getName());
        }
        return retMap;
    }

    public static OrderStatusEnum getByCode(String code) {
        OrderStatusEnum[] enumArr = OrderStatusEnum.values();
        for (OrderStatusEnum aEnum : enumArr) {
            if (aEnum.getCode().equals(code)) {
                return aEnum;
            }
        }
        return null;
    }
}
