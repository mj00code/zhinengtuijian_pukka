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
    * 管理员表
    * </p>
 *
 * @author gz_recommend
 * @since 2020-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_account")
public class AccountDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 账号
     */
    @TableField("account")
    private String account;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * md5密码盐
     */
    @TableField("salt")
    private String salt;

    /**
     * 名字
     */
    @TableField("name")
    private String name;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDateTime birthday;

    /**
     * 性别（1：男 2：女）
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 电子邮件
     */
    @TableField("email")
    private String email;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    @TableField("status")
    private Integer status;


}
