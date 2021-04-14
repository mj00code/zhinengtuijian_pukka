package com.ubo.iptv.entity.gdgd;

import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: xuning
 * @Date: 2020-11-02
 */
@Data
@NoArgsConstructor
public class UserDTO implements Serializable {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户code
     */
    private String userCode;

    /**
     * ServerID
     */
    private String serverId;

    /**
     * CityNum
     */
    private String cityNum;

    /**
     * 城市
     */
    private String cityName;

    /**
     * 状态
     */
    private String status;

    /**
     * 平台标示
     */
    private Integer platform;

    /**
     * 激活时间
     */
    private LocalDateTime activeTime;

    /**
     * 运营商
     */
    private String sysId;

    /**
     * 标签id
     */
    private Long[] tagId;

    /**
     * 标签
     */
    private String[] tag;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 移动gsgd_user转user
     *
     * @param gsydUserDO
     */
    public UserDTO(GSGsydUserDO gsydUserDO) {
        this.userId = gsydUserDO.getId();
        this.userCode = gsydUserDO.getIPTVUserID();
        this.serverId = gsydUserDO.getServerID();
        this.cityNum = gsydUserDO.getCityNum();
        this.cityName = gsydUserDO.getCityNumName();
        this.status = gsydUserDO.getStatus();
        this.platform = gsydUserDO.getPlatform();
        this.activeTime = gsydUserDO.getCreatetime();
    }
}
