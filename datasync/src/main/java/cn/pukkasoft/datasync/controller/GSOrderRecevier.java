package cn.pukkasoft.datasync.controller;


import cn.pukkasoft.datasync.advice.ApiResponse;
import cn.pukkasoft.datasync.advice.ApiResponseEnum;
import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.model.Order;
import cn.pukkasoft.datasync.service.OrderService;
import cn.pukkasoft.datasync.vo.GSOrderVo;
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
import java.util.List;


/**
 * @author majia:
 * @title: OrderController
 */
@RestController
@RequestMapping(value = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags="order信息")
@Slf4j
public class GSOrderRecevier {
    @Autowired
    private OrderService orderService;
    
	@ApiOperation(value="新增")
    @PostMapping("/recevierOrder")
    public ApiResponse recevierOrder(@Valid @RequestBody GSOrderVo orderVo) {
//        long apiStart = System.currentTimeMillis();
        ApiResponse api = new ApiResponse();
        Integer resultInt=0;
        try {
            if(orderVo.getPlatformOperator().trim().equalsIgnoreCase("CMCC")){
//                移动的订购
                List<Order> orders = orderService.selectByOrderNumber(PlatFormEnum.getByCode(orderVo.getPlatformOperator()).intEnumValue, orderVo.getOrderNumber());
                if(orders.size()>0){
//                    移动订购的修改
                    resultInt=orderService.updateDetailed(orderVo);
                }else{
//                    移动订购的新增
                    resultInt=orderService.insert(orderVo);
                }
            }else if(orderVo.getPlatformOperator().trim().equalsIgnoreCase("CUCC")){
//                联通的订购
                List<Order> orders = orderService.selectByOrderNumber(PlatFormEnum.getByCode(orderVo.getPlatformOperator()).intEnumValue, orderVo.getOrderNumber());
                if(orders.size()>0){
//                    联通订购的修改
                    resultInt=orderService.updateDetailed(orderVo);
                }else{
//                    联通订购的新增
                    resultInt=orderService.insert(orderVo);
                }
            }else{
//电信的订购
                List<Order> orders = orderService.selectByOrderNumber(PlatFormEnum.getByCode(orderVo.getPlatformOperator()).intEnumValue, orderVo.getOrderNumber());
                if(orders.size()>0){
//                    订购的修改
                    resultInt=orderService.updateDetailed(orderVo);
                }else{
//                    订购的新增
                    resultInt=orderService.insert(orderVo);
                }
            }
            if(resultInt>0){
                api.setCode(ApiResponseEnum.SUCCESS.code);
                api.setErrorMessage(ApiResponseEnum.SUCCESS.msg);
            }else{
                log.error("新增,修改失败");
                api.setCode(ApiResponseEnum.ERROR.code);
                api.setErrorMessage(ApiResponseEnum.ERROR.msg);
            }
        } catch (ParseException e) {
            log.error("数据字段不合法,解析异常");
            api.setCode(ApiResponseEnum.ERROR.getCode());
            api.setErrorMessage("内部服务器错误：数据字段不合法,解析异常");
            e.printStackTrace();
        }
//        System.out.println("api用时"+(System.currentTimeMillis()-apiStart));
        return  api;
    }


    
}
