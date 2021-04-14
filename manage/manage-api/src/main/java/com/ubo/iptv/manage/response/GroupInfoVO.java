package com.ubo.iptv.manage.response;

import lombok.Data;

@Data
public class GroupInfoVO {
    private Long groupId;//  #用户分群编号
    private String groupName;//  #用户分群名称
    private Long groupCount;// #用户分群数
    private String updateTime;// #更新时间
    private String createTime;//  #创建时间
    private String createUser;//  #创建人

}
