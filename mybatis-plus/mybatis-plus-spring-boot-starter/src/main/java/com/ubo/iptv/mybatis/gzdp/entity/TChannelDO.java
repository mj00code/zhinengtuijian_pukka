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
 * 频道
 * </p>
 *
 * @author gzdp
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.t_channel")
public class TChannelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 频道ID
     */
    @TableField("channel_id")
    private String channelId;

    /**
     * 频道名称
     */
    @TableField("name")
    private String name;

    /**
     * 频道CODE
     */
    @TableField("code")
    private String code;

    /**
     * 频道号
     */
    @TableField("channel_number")
    private String channelNumber;

    /**
     * 台标名称
     */
    @TableField("call_sign")
    private String callSign;

    /**
     * 时移标识
     */
    @TableField("time_shift")
    private String timeShift;

    /**
     * 存储时长，小时
     */
    @TableField("storage_duration")
    private String storageDuration;

    /**
     * 时移时长，分钟
     */
    @TableField("time_shift_duration")
    private String timeShiftDuration;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 省
     */
    @TableField("state")
    private String state;

    /**
     * 市
     */
    @TableField("city")
    private String city;

    /**
     * 邮编
     */
    @TableField("zip_code")
    private String zipCode;

    /**
     * 频道类型
     */
    @TableField("type")
    private String type;

    /**
     * 信号来源
     */
    @TableField("sub_type")
    private String subType;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 播放开始时间(HH24MI)
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 播放结束时间(HH24MI)
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 版权保护标识
     */
    @TableField("macrovision")
    private String macrovision;

    /**
     * 视频参数
     */
    @TableField("video_type")
    private String videoType;

    /**
     * 音频参数
     */
    @TableField("audio_type")
    private String audioType;

    /**
     * 码流参数
     */
    @TableField("stream_type")
    private String streamType;

    /**
     * 双语标识
     */
    @TableField("bilingual")
    private String bilingual;

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
     * Logo
     */
    @TableField("logo")
    private String logo;

    /**
     * 大海报
     */
    @TableField("poster")
    private String poster;

    /**
     * CPID
     */
    @TableField("cp_id")
    private Integer cpId;

    /**
     * 上线状态（VALID=在线状态；INVALID=下线状态）
     */
    @TableField("online_status")
    private String onlineStatus;

    /**
     * CP名称
     */
    @TableField("cp_name")
    private String cpName;

    /**
     * 播放地址
     */
    @TableField("play_url")
    private String playUrl;

    /**
     * 运营商标识（CTC=电信；CU=联通；CM=移动）
     */
    @TableField("isp")
    private String isp;

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
     * 审核人
     */
    @TableField("audit_by")
    private String auditBy;

    /**
     * 审核人ID
     */
    @TableField("audit_by_id")
    private Long auditById;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit_status_desc")
    private String auditStatusDesc;

    /**
     * 审核人
     */
    @TableField("audit2_by")
    private String audit2By;

    /**
     * 审核人ID
     */
    @TableField("audit2_by_id")
    private Long audit2ById;

    /**
     * 审核时间
     */
    @TableField("audit2_time")
    private LocalDateTime audit2Time;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit2_status")
    private String audit2Status;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit2_status_desc")
    private String audit2StatusDesc;

    /**
     * 审核人
     */
    @TableField("audit3_by")
    private String audit3By;

    /**
     * 审核人ID
     */
    @TableField("audit3_by_id")
    private Long audit3ById;

    /**
     * 审核时间
     */
    @TableField("audit3_time")
    private LocalDateTime audit3Time;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit3_status")
    private String audit3Status;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit3_status_desc")
    private String audit3StatusDesc;

    /**
     * 是否有小屏播放版权（YES=有版权；NO=无版权）
     */
    @TableField("is_copyright")
    private String isCopyright;

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

    private Integer mediaTypeId;
}
