package com.ubo.iptv.mybatis.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
    * 用户每日小结
    * </p>
 *
 * @author gz_recommend
 * @since 2020-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_group_daily_summary")
public class GroupDailySummaryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组id
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 组名称
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 组查询数量
     */
    @TableField("group_count")
    private Long groupCount;

    /**
     * 日期
     */
    @TableField("summary_date")
    private LocalDate summaryDate;

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
