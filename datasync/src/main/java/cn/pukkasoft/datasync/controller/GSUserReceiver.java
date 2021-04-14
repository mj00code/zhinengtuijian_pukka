package cn.pukkasoft.datasync.controller;


import cn.pukkasoft.datasync.advice.ApiResponse;
import cn.pukkasoft.datasync.advice.ApiResponseEnum;
import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.service.UserService;
import cn.pukkasoft.datasync.vo.GSUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;


/**
 * author:magj
 * date:2020/11/2
 */
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags="user信息")
@Slf4j
public class GSUserReceiver {
    @Autowired
    private UserService userService;



    @ApiOperation(value="甘肃用户同步")
    @PostMapping(value="/recevierUser",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse recevierUser(@Valid @RequestBody GSUserVo userVo) {
//        long start = System.currentTimeMillis();
        ApiResponse api = new ApiResponse();
        Integer resultInt=0;

        try {
            if(userService.selectByPlatOpreatorAndUserId(PlatFormEnum.getByCode(userVo.getPlatformOperator().trim().toUpperCase()).intEnumValue,userVo.getUserId())){
     //      如果已经存在该用户即修改用户信息
                  resultInt = userService.updateDetailed(userVo);
             }else{
     //      用户不存在，新增用户
                  resultInt = userService.insert(userVo);
             }
            if(resultInt>0){
                api.setCode(ApiResponseEnum.SUCCESS.code);
                api.setErrorMessage(ApiResponseEnum.SUCCESS.msg);
            }else{
                log.info("新增,修改失败");
                api.setCode(ApiResponseEnum.ERROR.code);
                api.setErrorMessage(ApiResponseEnum.ERROR.msg);
            }
        } catch (ParseException e) {
            log.error("数据字段不合法,解析异常");
            api.setCode(ApiResponseEnum.ERROR.getCode());
            api.setErrorMessage("内部服务器错误：数据字段不合法,解析异常");
            e.printStackTrace();
        }

//        System.out.println(System.currentTimeMillis()-start);
        return  api;
    }
}
