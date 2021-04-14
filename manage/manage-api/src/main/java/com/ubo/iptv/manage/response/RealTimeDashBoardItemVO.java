package com.ubo.iptv.manage.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RealTimeDashBoardItemVO {
    private Number sum;
    private List<Number> today = new ArrayList<>();
    private List<Number> yesterday = new ArrayList<>();
}
