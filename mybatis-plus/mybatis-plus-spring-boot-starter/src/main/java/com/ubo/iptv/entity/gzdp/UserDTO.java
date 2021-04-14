package com.ubo.iptv.entity.gzdp;

import com.ubo.iptv.mybatis.gzdp.entity.TpUserDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: magj
 * @Date: 2020-11-02
 */
@Data
@NoArgsConstructor
public class UserDTO implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNick;
    /**
     * 微信openId
     */
    private String wxOpenId;
    /**
     * avatar
     */
    private String avatar;


    /**
     * 城市
     */
    private String cityName;

    /**
     * 状态
     */
    private String status;


    /**
     * 微信性别
     */
    private String wxGender;

    /**
     * 微信手机号
     */
    private String wxPhoneNumber;

    /**
     * 标签id
     */
    private Long[] tagId;

    /**
     * 标签
     */
    private String[] tag;
    /**
     * 激活时间
     */
    private LocalDateTime activeTime;

    /**
     * 创建时间
     */
    private  String createTime;

    /**
     * 移动gsgd_user转user
     *
     * @param tpUserDO
     */
    public UserDTO(TpUserDO tpUserDO) {
        this.userId = tpUserDO.getId();
        this.userNick = tpUserDO.getNick();
        this.avatar = tpUserDO.getAvatar();
        this.cityName = tpUserDO.getWxCity();
        this.status = tpUserDO.getStatus();
        this.wxGender = tpUserDO.getWxGender();
        this.wxPhoneNumber = tpUserDO.getWxPhoneNumber();
        this.wxOpenId=tpUserDO.getWxOpenid();
        this.activeTime=tpUserDO.getGmtCreate();
    }
}
