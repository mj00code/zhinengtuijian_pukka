package com.ubo.iptv.manage.response;

import lombok.Data;

import java.util.List;

@Data
public class DailyDashBoardVO {
    private List<String> keys;
    private Number sum;
    private List<? extends Number> values;
}
