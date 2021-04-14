package com.ubo.iptv.mybatis.gsgd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
    * 
    * </p>
 *
 * @author ottdb_gsgd
 * @since 2020-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_channel_pirurl")
public class GSChannelPirurlDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("channel_name")
    private String channelName;

    /**
     * 图片地址
     */
    @TableField("pic_url")
    private String picUrl;

    /**
     * 业务侧code
     */
    @TableField("yw_channel_code")
    private String ywChannelCode;

    /**
     * 运营商标识1移动2联通
     */
    @TableField("platform")
    private String platform;

    /**
     * 1标清2高清3HD
     */
    @TableField("definition")
    private String definition;

    /**
     * 播控侧code
     */
    @TableField("bk_channel_code")
    private String bkChannelCode;


}
