package cn.pukkasoft.datasync.controller;

import cn.pukkasoft.datasync.advice.ApiResponse;
import cn.pukkasoft.datasync.service.ITContentPicUrlService;
import cn.pukkasoft.datasync.vo.ContentInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author SHAWN LIAO
 * @ClassName ContentInfoSyscController
 * @Date 2020/11/22 15:15
 * @Description 媒资及图片同步
 */

@RestController
@Slf4j
@Api(tags = "媒资图片信息同步")
@RequestMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ContentInfoSysController {

    @Autowired
    private ITContentPicUrlService contentPicUrlService;

    @ApiOperation(value = "新增")
    @PostMapping("/notifyContentStatus")
    public ApiResponse notifyContentStatus(@RequestBody ContentInfoVo contentInfoVo) {
        ApiResponse apiResponse = new ApiResponse();
        //  if (StringUtils.isNotEmpty(contentInfoVo.getContentCode())) {
        try {
            //判断媒资是否存在
//                TContentPicUrl contentPicUrl = new TContentPicUrl();
//                contentPicUrl.setContentCode(contentInfoVo.getContentCode());
//                int count = contentPicUrlService.getContentPicCount(contentInfoVo.getContentCode());
//                //媒资-图片关系存在
//                if (count == 1) {
//                    //修改图片关系记录
//                    apiResponse = contentPicUrlService.notifyContentStatus(contentInfoVo, 0);
//                }
//                if (count > 1) {
//                    //数据异常
//                    apiResponse.setCode(0);
//                    apiResponse.setErrorMessage("数据异常");
//                }
//                if (count == 0) {
//                    //新增图片关系记录
//                    apiResponse = contentPicUrlService.notifyContentStatus(contentInfoVo, 1);
//                }
            apiResponse = contentPicUrlService.notifyContentStatus(contentInfoVo.getContentType(), contentInfoVo.getContentCode(), contentInfoVo.getContentId());
        } catch (Exception e) {
            apiResponse.setCode(0);
            apiResponse.setErrorMessage("数据异常");
        }
        //  }
        return apiResponse;
    }
}