package com.ubo.iptv.manage.response;

import lombok.Data;

@Data
public class UserBussinessInfo {
    private Integer orderCount;// #订购次数
    private Integer movieVipDays;//#电影VIP剩余天数
    private Integer seriesVipDays;// #电视剧VIP剩余天数
    private Integer varietyVipDays;// #综艺VIP剩余天数
    private Integer comicVipDays;//  #动漫VIP剩余天数
    private Integer childrenVipDays;// #少儿VIP剩余天数

}
