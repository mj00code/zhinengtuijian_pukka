package com.ubo.iptv.core.enums;


import com.ubo.iptv.core.annotation.CodeEntity;

/**
 * @author huangjian
 */

public enum CommonCodeEnum implements CodeEnum {
    @CodeEntity(result = 0, msg = "ok")
    GLOBAL_SUCCESS,
    @CodeEntity(result = -1, status = -1, msg = "服务器开小差，请稍后再试!")
    GLOBAL_FAIL,
    @CodeEntity(result = -1, status = -2, msg = "网络打了个喷嚏，请稍后再试!")
    GLOBAL_FALL_BACK,
    @CodeEntity(result = -1, status = -3, msg = "您的手速太快了，请稍后再试!")
    GLOBAL_BUSY,
    @CodeEntity(result = 888888, msg = "用户未注册，请先注册!")
    GLOBAL_NO_USER,
    @CodeEntity(result = 999999, msg = "用户未登录，请重新登录!")
    GLOBAL_NO_LOGIN,
    @CodeEntity(result = 403, status = 403101, msg = "没有访问权限!")
    GLOBAL_NO_PERMISSION,
    @CodeEntity(result = 403, status = 403102, msg = "该访问已失效!")
    GLOBAL_EXPIRED,
    @CodeEntity(result = 500, status = 500101, msg = "%s不存在")
    GLOBAL_RECORD_NOT_EXIST,
    @CodeEntity(result = 500, status = 500102, msg = "%s新增失败")
    GLOBAL_RECORD_INSERT_FAILED,
    @CodeEntity(result = 500, status = 500103, msg = "%s更新失败")
    GLOBAL_RECORD_UPDATE_FAILED,
    @CodeEntity(result = 500, status = 500104, msg = "%s保存失败")
    GLOBAL_RECORD_SAVE_FAILED,
    @CodeEntity(result = 500, status = 500105, msg = "%s删除失败")
    GLOBAL_RECORD_DELETE_FAILED,
    @CodeEntity(result = 500, status = 500106, msg = "%s获取失败")
    GLOBAL_RECORD_GET_FAILED,
    @CodeEntity(result = 500, status = 500201, msg = "%s解密失败")
    GLOBAL_DECODE_FAILED
}
