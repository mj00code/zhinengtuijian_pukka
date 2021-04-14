package cn.pukkasoft.datasync.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author SHAWN LIAO
 * @ClassName ContentInfoVo
 * @Date 2020/11/22 15:23
 * @Description 同步参数
 */
@Data
@ApiModel("ContentInfoVo")
public class ContentInfoVo {

    @ApiModelProperty(value = "节目code", dataType = "String", name = "contentCode")
    private String contentCode;

    @ApiModelProperty(value = "节目名称", dataType = "String", name = "contentName")
    private String contentName;

    @ApiModelProperty(value = "下游平台标识", dataType = "Integer", name = "branchCode")
    private Integer branchCode;

    @ApiModelProperty(value = "内容状态 ##IsSearch ##SelectList{0:下线,1:上线}", dataType = "Integer", name = "status")
    private Integer status;

    @ApiModelProperty(value = "播放地址", dataType = "String", name = "playUrl")
    private String playUrl;

    @ApiModelProperty(value = "第三方节目code", dataType = "String", name = "extContentCode")
    private String extContentCode;

    @ApiModelProperty(value = "媒资类型 ##IsSearch ##SelectList{1:电影,2:子集，3：电视剧，4：直播，5：系列片，6：片花}", dataType = "Integer", name = "contentType")
    private Integer contentType;

    @ApiModelProperty(value = "contentinfo表contentId", dataType = "Integer", name = "contentId")
    private Integer contentId;

}
