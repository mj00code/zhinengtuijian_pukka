package com.ubo.iptv.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ubo.iptv.core.enums.CodeEnum;
import com.ubo.iptv.core.enums.CommonCodeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对于需要返回多个entity对象时如何填充到data，可以按以下方法处理：
 * 将多个对象包装成一个新的entity，或者直接用JSONObject来包装。
 * Created by zhouyu on 2016/10/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"result", "status", "msg", "data"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> extends AbstractCommonResponse<T> {

    @ApiModelProperty(value = "主状态", example = "0")
    private int result;

    @ApiModelProperty(value = "子状态", example = "0")
    private int status;

    @ApiModelProperty(value = "描述信息", example = "ok")
    private String msg;

    @ApiModelProperty(value = "返回Json对象")
    private T data;

    /**
     * 不需要返回数据的情形
     *
     * @param codeEnum
     * @param msgArgs
     * @return
     */
    public static CommonResponse build(CodeEnum codeEnum, Object... msgArgs) {
        return CommonResponse.builder().result(codeEnum.result()).status(codeEnum.status()).msg(codeEnum.msg_client(msgArgs)).build();
    }

    /**
     * 需要返回数据的情形
     *
     * @param codeEnum
     * @param data
     * @param msgArgs
     * @return
     */
    public static <T> CommonResponse<T> buildWithData(CodeEnum codeEnum, T data, Object... msgArgs) {
        return CommonResponse.<T>builder().result(codeEnum.result()).status(codeEnum.status()).msg(codeEnum.msg_client(msgArgs)).data(data).build();
    }

    public static CommonResponse SUCCESS = CommonResponse.build(CommonCodeEnum.GLOBAL_SUCCESS);
    public static CommonResponse FAIL = CommonResponse.build(CommonCodeEnum.GLOBAL_FAIL);
    public static CommonResponse FALL_BACK = CommonResponse.build(CommonCodeEnum.GLOBAL_FALL_BACK);

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.buildWithData(CommonCodeEnum.GLOBAL_SUCCESS, data);
    }

    public static CommonResponse fail(int result, String msg) {
        return CommonResponse.builder().result(result).msg(msg).build();
    }
}
