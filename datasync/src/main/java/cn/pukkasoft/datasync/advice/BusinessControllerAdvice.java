package cn.pukkasoft.datasync.advice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xiayufeng
 * @title:
 * @Date: 2020/6/12 0012 上午 8:52
 */
@ControllerAdvice
@Slf4j
public class BusinessControllerAdvice {
    /**
     * 业务异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = ApiResponseException.class)
    @ResponseBody
    public ApiResponse handleApiResponseException(ApiResponseException ex) {
        ex.printStackTrace();
        ApiResponse api = new ApiResponse();
        api.setCode(ex.getCode());
        api.setErrorMessage(ex.getMessage());
        return api;
    }

    /**
     * 参数类型异常处理：比如要求int传英文字符就会抛出此异常
     *
     * @param ex
     * @return
     */

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseBody
    public ApiResponse handleMethodValidException(HttpMessageNotReadableException ex) {
        ex.printStackTrace();
        ApiResponse api = new ApiResponse();
//        api.setCode(ApiResponseEnum.ERROR_PARAMETER.getCode());
        api.setCode(ApiResponseEnum.ERROR.getCode());
        api.setErrorMessage(ApiResponseEnum.ERROR_PARAMETER.msg + "：注意检查参数类型是否正确");
        return api;
    }

    /**
     * 参数异常处理
     *
     * @param ex
     * @return
     */

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseBody
    public ApiResponse handleMethodValidException(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        ApiResponse api = new ApiResponse();
//        api.setCode(ApiResponseEnum.ERROR_PARAMETER.getCode());
        api.setCode(ApiResponseEnum.ERROR.getCode());
        api.setErrorMessage(ex.getBindingResult().getFieldError().getDefaultMessage());
        return api;
    }


    @ExceptionHandler(value = {BindException.class})
    @ResponseBody
    public ApiResponse handleBindException(BindException ex) {
        ex.printStackTrace();
        ApiResponse api = new ApiResponse();
//        api.setCode(ApiResponseEnum.ERROR_PARAMETER.getCode());
        api.setCode(ApiResponseEnum.ERROR.getCode());
        api.setErrorMessage(ex.getBindingResult().getFieldError().getDefaultMessage());
        return api;
    }

    /**
     * 全局异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResponse handleException(Exception ex) {
        ex.printStackTrace();
        log.info(ex.getMessage());
//        ApiResponse api = new ApiResponse(ApiResponseEnum.SERVER_INTERNAL_ERROR.getCode(), ApiResponseEnum.SERVER_INTERNAL_ERROR.getMsg());
        ApiResponse api = new ApiResponse(ApiResponseEnum.ERROR.getCode(), ApiResponseEnum.SERVER_INTERNAL_ERROR.getMsg());
        return api;
    }
}
