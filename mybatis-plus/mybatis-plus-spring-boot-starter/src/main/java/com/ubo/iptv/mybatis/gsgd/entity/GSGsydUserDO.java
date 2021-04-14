package com.ubo.iptv.mybatis.gsgd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
    * 
    * </p>
 *
 * @author ottdb_gsgd
 * @since 2020-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_gsyd_user")
public class GSGsydUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("ServerID")
    private String ServerID;

    @TableField("Phone")
    private String Phone;

    @TableField("Timestamp")
    private LocalDateTime Timestamp;

    @TableField("Status")
    private String Status;

    @TableField("CityNum")
    private String CityNum;

    @TableField("AreaNum")
    private String AreaNum;

    @TableField("ChannelNum")
    private String ChannelNum;

    @TableField("JuXiangNum")
    private String JuXiangNum;

    @TableField("PeopleNum")
    private String PeopleNum;

    @TableField("SPNum")
    private String SPNum;

    @TableField("OPNum")
    private String OPNum;

    @TableField("DeviceNum")
    private String DeviceNum;

    @TableField("CorrelateID")
    private String CorrelateID;

    @TableField("OTTUserID")
    private String OTTUserID;

    @TableField("IPTVUserID")
    private String IPTVUserID;

    @TableField("Password")
    private String Password;

    @TableField("SN")
    private String sn;

    @TableField("createtime")
    private LocalDateTime createtime;

    @TableField("updatetime")
    private LocalDateTime updatetime;

    @TableField("token")
    private String token;

    @TableField("UserGroupId")
    private String UserGroupId;

    /**
     * 用户分组名称
     */
    @TableField("UserGroupName")
    private String UserGroupName;

    /**
     * 平台标示1：移动2：联通
     */
    @TableField("platform")
    private Integer platform;

    /**
     * 激活时间
     */
    @TableField("activetime")
    private LocalDateTime activetime;

    /**
     * 地市号名称
     */
    @TableField("cityNumName")
    private String cityNumName;


}
