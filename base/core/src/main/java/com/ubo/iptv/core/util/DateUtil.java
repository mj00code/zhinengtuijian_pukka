package com.ubo.iptv.core.util;

import com.ubo.iptv.core.constant.ElasticSearchConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.joda.LocalDateParser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * @Author: xuning
 * @Date: 2018/12/4
 */
public class DateUtil {

    public static String defaultPattern = "yyyy-MM-dd HH:mm:ss";

    public static String format(Date date, String pattern) {
        if (date != null && StringUtils.isNotBlank(pattern)) {
            return new SimpleDateFormat(pattern).format(date);
        }
        return null;
    }

    public static String format(Date date) {
        return format(date, defaultPattern);
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime != null && StringUtils.isNotBlank(pattern)) {
            return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
        }
        return null;
    }


    public static String format(LocalDate localDate, String pattern) {
        if (localDate != null && StringUtils.isNotBlank(pattern)) {
            return DateTimeFormatter.ofPattern(pattern).format(localDate);
        }
        return null;
    }

    public static String format(LocalDateTime localDateTime) {
        return format(localDateTime, defaultPattern);
    }

    public static LocalDateTime parse(String source, String pattern) {
        if (StringUtils.isNotBlank(source) && StringUtils.isNotBlank(pattern)) {
            LocalDateTime localDateTime = LocalDateTime.parse(source, DateTimeFormatter.ofPattern(pattern));
            // nano==0时，JSONObject.toString()会默认转换成yyyy-MM-dd HH:mm:ss格式，这里加上1纳秒避免该情况
            if (localDateTime.getNano() == 0) {
                localDateTime = localDateTime.with(ChronoField.NANO_OF_SECOND, 1L);
            }
            return localDateTime;
        }
        return null;
    }

    public static LocalDateTime parse(String source) {
        return parse(source, defaultPattern);
    }

    /**
     * 取大值
     *
     * @param a
     * @param b
     * @return
     */
    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.isAfter(b) ? a : b;
    }

    /**
     * 取小值
     *
     * @param a
     * @param b
     * @return
     */
    public static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.isBefore(b) ? a : b;
    }

    public static String getEsIndexByDate(LocalDate date) {
        String suffix = format(date, "yyyyMMdd");
        return String.format(ElasticSearchConstant.template_log_index, suffix);
    }
}
