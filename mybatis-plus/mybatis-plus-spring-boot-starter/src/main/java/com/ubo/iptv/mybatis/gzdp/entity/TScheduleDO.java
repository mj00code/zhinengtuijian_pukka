package com.ubo.iptv.mybatis.gzdp.entity;

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
 * 频道节目单
 * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.t_schedule")
public class TScheduleDO  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer inId;

    /**
     * 节目单ID
     */
    @TableField("schedule_id")
    private String scheduleId;

    /**
     * 节目单code
     */
    @TableField("schedule_code")
    private String scheduleCode;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private String channelId;

    /**
     * 频道code
     */
    @TableField("channel_code")
    private String channelCode;

    /**
     * 节目名称
     */
    @TableField("program_name")
    private String programName;

    /**
     * 时长
     */
    @TableField("duration")
    private String duration;

    /**
     * 分类
     */
    @TableField("genre")
    private String genre;

    /**
     * 结束日期
     */
    @TableField("start_date_dt")
    private LocalDateTime startDateDt;

    /**
     * 结束日期
     */
    @TableField("end_date_dt")
    private LocalDateTime endDateDt;

    /**
     * 开始日期（yyyyMMdd）
     */
    @TableField("start_date")
    private String startDate;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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
     * 周几（星期一=MON；星期二 =TUE,星期三 =WED,星期四 =THU,星期五 =FRI,星期六=SAT,星期天=SUN）
     */
    @TableField("schedule_week")
    private String scheduleWeek;

    /**
     * 运营商标识（CTC=电信；CU=联通；CM=移动）
     */
    @TableField("isp")
    private String isp;

    /**
     * CPID
     */
    @TableField("cpid")
    private Long cpid;

    /**
     * CP名称
     */
    @TableField("cpname")
    private String cpname;

    /**
     * SPID
     */
    @TableField("spid")
    private Integer spid;

    /**
     * SP名称
     */
    @TableField("spname")
    private String spname;
    /**
     * 频道名称
     */
    private String channelName;

    private Integer categoryId;

    private String categoryName;

    private String contentType;

    /**
     * 搜索次数
     */
    private Integer searchCount;

    /**
     * 播放次数
     */
    private Integer recommendCount;

}
