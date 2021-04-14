package com.ubo.iptv.entity.gdgd;

import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class RecommendSnapshotDTO {

    //用户id+ sceneId + 时间
    private String key;
    //场景id
    private String sysId;
    //用户id
    private String userId;
    //运营商id
    private Long sceneId;
    //运营商id
    private String time;
    //场景策略信息
    private SceneDTO sceneDTO;
    //推荐内容
    private String recommendContent;
    //是否是新用户
    private Boolean isNewUser;
}
