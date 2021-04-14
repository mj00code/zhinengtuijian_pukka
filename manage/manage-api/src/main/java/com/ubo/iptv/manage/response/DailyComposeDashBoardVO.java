package com.ubo.iptv.manage.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DailyComposeDashBoardVO {
    private List<String> dates;
    private Map<String, List<String>> productNameMap;
    private DailyDashBoardVO productTypes;
    private Integer openCount;
    private Integer viewCount;
    private Integer orderCount;
    private String rate1 = "0%";
    private String rate2 = "0%";
    private String rate3 = "0%";


    public void composeRate() {
//        openCount = 10000;
//        viewCount = 7000;
//        orderCount = 1500;
        if (viewCount > 0 && openCount > 0) {
            BigDecimal a = new BigDecimal(viewCount).divide(new BigDecimal(openCount), 2, BigDecimal.ROUND_DOWN);
            rate1 = a.movePointRight(2).toString() + "%";
        }
        if (orderCount > 0 && viewCount > 0) {
            BigDecimal a = new BigDecimal(orderCount).divide(new BigDecimal(viewCount), 2, BigDecimal.ROUND_DOWN);
            rate2 = a.movePointRight(2).toString() + "%";
        }
        if (orderCount > 0 && openCount > 0) {
            BigDecimal a = new BigDecimal(orderCount).divide(new BigDecimal(openCount), 2, BigDecimal.ROUND_DOWN);
            rate3 = a.movePointRight(2).toString() + "%";
        }

    }
}
