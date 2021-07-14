package com.example.redwars.exception;

import com.example.redwars.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;


/**
 * 这个类是用做全局异常监控
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 目前只有一个简单的试用方法，其他以后待扩充
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, Exception e){
        log.error("其他异常！原因是:",e);
        return Result.fail(req.getRequestURI() + "500 服务器内部异常");
    }

    @ExceptionHandler(value = QuantitativeLogicException.class)
    @ResponseBody
    public Result quantitativeLogicExceptionHandler(HttpServletRequest req , QuantitativeLogicException e){
        log.error(req.getRequestURI() + ":数字逻辑异常",e.getMsg());
        return Result.fail(e.getMessage());
    }
}
