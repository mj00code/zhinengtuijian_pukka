package com.ubo.iptv.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ubo.iptv.core.enums.CodeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * websocket通用返回值
 * @author huangjian
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"result","status","method","msg","data"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WSResponse<T> extends AbstractCommonResponse<T> {

    @ApiModelProperty(value = "子状态", example = "0")
    private int status;

    @ApiModelProperty(value = "主状态", example = "0")
    private int result;

    @ApiModelProperty(value = "方法名",example = "public")
    private String method;

    @ApiModelProperty(value = "描述信息", example = "ok")
    private String msg;

    @ApiModelProperty(value = "返回Json对象")
    private T data;

    /**
     * 不需要返回数据的情形
     * @param codeEnum
     * @param msgArgs
     * @return
     */
    public static WSResponse build(CodeEnum codeEnum, String method, Object... msgArgs){
        return WSResponse.builder().method(method).result(codeEnum.result()).status(codeEnum.status()).msg(codeEnum.msg_client(msgArgs)).build();
    }

    /**
     * 需要返回数据的情形
     * @param codeEnum
     * @param data
     * @param msgArgs
     * @return
     */
    public static <T> WSResponse<T> buildWithData(CodeEnum codeEnum, String method, T data, Object... msgArgs){
        return new WSResponse<T>(codeEnum.status(), codeEnum.result(), method, codeEnum.msg_client(msgArgs), data);
    }
}
