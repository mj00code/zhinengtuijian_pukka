package com.ubo.iptv.manage.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TVComposeDashBoardVO {
    private List<String> dates;
    private Map<String, List<String>> userValues;
    private Map<String, List<String>> timeValues;
}

