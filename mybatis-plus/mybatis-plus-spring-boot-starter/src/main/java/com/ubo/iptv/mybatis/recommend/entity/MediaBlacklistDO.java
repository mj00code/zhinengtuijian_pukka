package com.ubo.iptv.mybatis.recommend.entity;

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
    * 媒资黑名单
    * </p>
 *
 * @author gz_recommend
 * @since 2021-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_media_blacklist")
public class MediaBlacklistDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运营商id
     */
    @TableField("sys_id")
    private String sysId;

    /**
     * 媒资id
     */
    @TableField("media_id")
    private Integer mediaId;

    /**
     * 媒资类型
     */
    @TableField("media_type")
    private Integer mediaType;

    /**
     * 媒资名称
     */
    @TableField("media_name")
    private String mediaName;

    /**
     * 媒资code
     */
    @TableField("media_code")
    private String mediaCode;

    /**
     * 状态：0-不生效；1-生效
     */
    @TableField("status")
    private Boolean status;

    /**
     * 生效开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;


}
