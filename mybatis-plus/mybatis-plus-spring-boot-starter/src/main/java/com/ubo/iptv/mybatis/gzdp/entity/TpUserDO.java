package com.ubo.iptv.mybatis.gzdp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
    * 用户
    * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.tp_user")
public class TpUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    @TableField("nick")
    private String nick;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 用户令牌
     */
    @TableField("user_token")
    private String userToken;

    /**
     * 微信session key
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 微信session key 失效时间
     */
    @TableField("session_expires")
    private LocalDateTime sessionExpires;

    /**
     * 是否测试账号(yes=测试账号；no=正式账号)
     */
    @TableField("is_test")
    private String isTest;

    /**
     * 微信昵称
     */
    @TableField("wx_nick")
    private String wxNick;

    /**
     * 微信城市
     */
    @TableField("wx_city")
    private String wxCity;

    /**
     * 微信省份
     */
    @TableField("wx_province")
    private String wxProvince;

    /**
     * 微信国家
     */
    @TableField("wx_country")
    private String wxCountry;

    /**
     * 微信头像地址
     */
    @TableField("wx_avatar_url")
    private String wxAvatarUrl;

    /**
     * 微信openid
     */
    @TableField("wx_openid")
    private String wxOpenid;

    /**
     * 微信unionid
     */
    @TableField("wx_unionid")
    private String wxUnionid;

    /**
     * 微信性别(male=男；female=女)
     */
    @TableField("wx_gender")
    private String wxGender;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    @TableField("msg_count")
    private Integer msgCount;

    @TableField("push_status")
    private String pushStatus;

    /**
     * 用户类型（FAKE=马甲用户；NORMAL=正式用户）
     */
    @TableField("user_type")
    private String userType;

    /**
     * 状态（VALID=有效；INVALID=无效）
     */
    @TableField("status")
    private String status;

    /**
     * 微信手机号
     */
    @TableField("wx_phone_number")
    private String wxPhoneNumber;

    /**
     * 微信手机号国家区号
     */
    @TableField("wx_phone_country_code")
    private String wxPhoneCountryCode;


}
