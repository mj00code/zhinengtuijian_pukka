package com.ubo.iptv.manage.response;

import lombok.Data;

@Data
public class RealTimeDashBoardVO {
    private String date;
    private RealTimeDashBoardItemVO online;
    private RealTimeDashBoardItemVO vodPlay;
    private RealTimeDashBoardItemVO tvPlay;
    private RealTimeDashBoardItemVO payAmount;
    private RealTimeDashBoardItemVO payUser;
    private RealTimeDashBoardItemVO payCounts;
}
