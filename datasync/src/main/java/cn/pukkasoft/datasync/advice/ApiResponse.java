package cn.pukkasoft.datasync.advice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuy
 * @title: ApiResponse
 * @description: 统一返回
 * @date 2019/6/12 14:11
 */
@Data
@ApiModel
public class ApiResponse{

    @ApiModelProperty(value="返回码",dataType="Integer",name="code",example="200")
    private Integer code;
    @ApiModelProperty(value="描述信息",dataType="String",name="msg",example="sucess")
    private String errorMessage;


    public ApiResponse(){
    }


    public ApiResponse(Integer code,String msg){
        this.code = code;
        this.errorMessage = msg;
    }


    public ApiResponse appendMsg(String appendMsg){
        this.errorMessage = SafeUtil.getString(this.errorMessage) +"。"+ SafeUtil.getString( appendMsg);
        return this;
    }

    static String joinMsg(ApiResponseEnum responseEnum,String msg){
        return StringUtils.join(responseEnum.getMsg(),":",msg);
    }

}
