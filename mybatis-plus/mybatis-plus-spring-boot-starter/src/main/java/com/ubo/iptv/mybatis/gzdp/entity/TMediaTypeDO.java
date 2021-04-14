package com.ubo.iptv.mybatis.gzdp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author SHAWN LIAO
 * @ClassName TMediaType
 * @Date 2021/3/9 13:52
 * @Description 媒资类型
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.t_media_type")
public class TMediaTypeDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("media_type_id")
    private Integer mediaTypeId;
    @TableField("media_type_name")
    private String mediaTypeName;
    @TableField("category_id")
    private String categoryId;

}
