package com.ubo.iptv.manage.enums;

import com.ubo.iptv.core.annotation.CodeEntity;
import com.ubo.iptv.core.enums.CodeEnum;

/**
 * @author huangjian
 */
public enum ManageErrorEnum implements CodeEnum {

    @CodeEntity(status = 806001, result = 1, msg = "用户名不存在")
    USERNAME_NOT_EXIST,

    @CodeEntity(status = 806002, result = 1, msg = "密码不正确")
    PASSWORD_NOT_CORRECT,

    @CodeEntity(status = 806005, result = 1, msg = "登录失败")
    LOGIN_FAILED,
    @CodeEntity(status = 806006, result = 1, msg = "该媒资没有海报信息,不能人工干预!")
    NO_POSTER_URL,
    @CodeEntity(status = 806007, result = 1, msg = "该分组信息不存在!")
    GROUP_NOT_EXSIT,
    @CodeEntity(status = 806008, result = 1, msg = "该媒资处于拉黑中,不支持当前操作!")
    AREADY_EXSIT_IN_BLACK_LIST,
    @CodeEntity(status = 806009, result = 1, msg = "该媒资不是发布状态,不支持当前操作!")
    PUBLISH_ERROR,
    @CodeEntity(status = 806010, result = 1, msg = "至少要选中一个标签!")
    NON_TAG_SELECT,
    @CodeEntity(status = 806011, result = 1, msg = "该分组信息的条件不存在,无法解析!")
    GROUP_CONDITION_NOT_EXSIT,
}