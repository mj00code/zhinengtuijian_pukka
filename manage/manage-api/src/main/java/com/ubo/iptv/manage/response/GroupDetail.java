package com.ubo.iptv.manage.response;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class GroupDetail {
    //时间
    String date;
    //时间
    Long count;
    List<UserGroupTagInfo> list;
    Set<String> tagList;

}
