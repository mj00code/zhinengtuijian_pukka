package com.ubo.iptv.core.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @Author: xuning
 * @Date: 2018/12/11
 */
public class BigDecimalUtil {

    public static String format(BigDecimal decimal, String pattern) {
        if (decimal != null && StringUtils.isNotBlank(pattern)) {
            return new DecimalFormat(pattern).format(decimal);
        }
        return null;
    }

    public static String format(BigDecimal decimal) {
        return format(decimal, "0.00");
    }

    /**
     * 取大值
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) > 0 ? a : b;
    }

    /**
     * 取小值
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) < 0 ? a : b;
    }


    /**
     * 取两数的百分比
     *
     * @param a
     * @param b
     * @param scare
     * @return
     */
    public static BigDecimal rate(Number a, Number b, int scare) {
        if (a == null || b == null) {
            return null;
        }
        BigDecimal bb = new BigDecimal(b.toString());
        if (BigDecimal.ZERO.equals(bb)) {
            return null;
        }
        BigDecimal ba = new BigDecimal(a.toString());
        return ba.movePointRight(2).divide(bb, scare, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 取两数的比值，保留2位小数
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal rate(Number a, Number b) {
        return rate(a, b, 2);
    }
}
