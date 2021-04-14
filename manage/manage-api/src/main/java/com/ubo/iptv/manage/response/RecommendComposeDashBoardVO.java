package com.ubo.iptv.manage.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RecommendComposeDashBoardVO {
    private List<String> dates;
    private Map<String, List<String>> sceneData;
    private Map<String, List<String>> mediaData;
}

