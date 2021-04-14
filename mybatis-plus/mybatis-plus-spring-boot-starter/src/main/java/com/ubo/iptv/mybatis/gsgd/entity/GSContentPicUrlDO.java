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
    * 媒资图片关系表
    * </p>
 *
 * @author ottdb_gsgd
 * @since 2020-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_content_pic_url")
public class GSContentPicUrlDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 媒资类型
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 内容code
     */
    @TableField("content_code")
    private String contentCode;

    /**
     * 媒资名称
     */
    @TableField("media_name")
    private String mediaName;

    /**
     * 图片地址
     */
    @TableField("pic_url")
    private String picUrl;

    /**
     * 运营商标识1移动2联通
     */
    @TableField("platform")
    private Integer platform;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 收费角标地址
     */
    @TableField("corner_picurl")
    private String cornerPicurl;

    @TableField("cpid")
    private Integer cpid;

    @TableField("spid")
    private Integer spid;

    /**
     * cp名称
     */
    @TableField("cp_name")
    private String cpName;

    /**
     * sp名称
     */
    @TableField("sp_name")
    private String spName;


}
