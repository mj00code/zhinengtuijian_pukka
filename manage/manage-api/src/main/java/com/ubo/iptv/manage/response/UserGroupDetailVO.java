package com.ubo.iptv.manage.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserGroupDetailVO {
    //用户分群历史趋势
    List<GroupDetail> countHistory;
    //活跃特性
    List<GroupDetail> activeHistory;
    //付费能力
    List<GroupDetail> orderAbilityHistory;
    //订购类型
    List<GroupDetail>  orderTypeHistory;
    //偏好特性
    List<GroupDetail>  preferHistory;
    //用户列表
    List<TagUserSummaryVO> userList;

}
