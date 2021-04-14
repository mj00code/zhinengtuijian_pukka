package cn.pukkasoft.datasync.advice;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author:magj
 * date:2020/11/3
 */
public class TimeUtils {
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static String parseDate(String dateStr) throws ParseException {
        if (null==dateStr||"".equals(dateStr)) {
            return "";
        }
        //解析规则：验证时间格式用到：org.apache.commons.lang3.time.DateUtils里面的parseDate方法，具体调用为：
        Date date = DateUtils.parseDate(dateStr, Locale.ENGLISH,
                "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "MMM d, yyyy K:m:s a", "MMM d, yyyy K:m:s a",
                "yyyy-MM-dd HH:mm", "yyyyMMddHHmm", "yyyy/MM/dd HH:mm", "yyyy.MM.dd HH:mm", "yyyy/MM/dd", "yyyyMMdd", "yyyy.MM.dd", "yyyy-MM-dd",
                "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy.MM");
        return format.format(date);
    }

    public static String parseDate(Date date){
        return format.format(date);
    }
}
