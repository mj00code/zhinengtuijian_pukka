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
    * 用户收藏
    * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.tp_user_favorite")
public class TpUserFavoriteDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 内容id
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容code
     */
    @TableField("content_code")
    private String contentCode;

    /**
     * 内容名称
     */
    @TableField("content_name")
    private String contentName;

    /**
     * 内容海报url
     */
    @TableField("content_poster_url")
    private String contentPosterUrl;

    /**
     * 内容类型(program=电影；series=剧集)
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 内容总集数
     */
    @TableField("content_volumn_count")
    private String contentVolumnCount;

    /**
     * 内容总时长
     */
    @TableField("content_duration")
    private String contentDuration;

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

    /**
     * 用户类型：微信小程序=WX; EPG=EPG;手机app=APP;
     */
    @TableField("user_origin")
    private String userOrigin;

    /**
     * 运营商标识（CTC=电信；CU=联通；CM=移动）
     */
    @TableField("isp")
    private String isp;

    /**
     * 收藏来源（WXAPP=微信小程序；EPG=EPG）
     */
    @TableField("origin")
    private String origin;


}
