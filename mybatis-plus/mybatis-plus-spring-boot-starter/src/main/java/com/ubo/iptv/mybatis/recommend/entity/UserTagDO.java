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
    * 
    * </p>
 *
 * @author gz_recommend
 * @since 2020-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_user_tag")
public class UserTagDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 0:用户标签 1:媒资标签
     */
    @TableField("classify")
    private Integer classify;

    /**
     * 0:事实标签 1:活跃特征 2:订购类型特征 3:付费能力特征 4:偏好特征
     */
    @TableField("type")
    private Integer type;

    /**
     * 标签划分
     */
    @TableField("divide")
    private String divide;

    /**
     * 标签id
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
