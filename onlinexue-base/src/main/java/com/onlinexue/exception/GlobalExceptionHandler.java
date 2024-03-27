package com.onlinexue.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //对项目自定义异常处理
    @ExceptionHandler(OnlineXuePlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(OnlineXuePlusException e) {
        //记录异常
        log.error("系统异常:{}", e.getErrMessage(), e);
        //解析异常
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse();
        restErrorResponse.setErrMessage(errMessage);
        return restErrorResponse;
    }

    //数据库异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        //记录异常
        log.error("系统异常:{}", e.getMessage(), e);
        //解析异常
        RestErrorResponse restErrorResponse = new RestErrorResponse();
        restErrorResponse.setErrMessage(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;

    }

    //数据库异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        //存放错误信息
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item -> {
            errors.add(item.getDefaultMessage());
        });
        String errMessage = StringUtils.join(errors, ",");
        //记录异常
        log.error("系统异常:{}", e.getMessage(), errMessage);
        //解析异常
        RestErrorResponse restErrorResponse = new RestErrorResponse();
        restErrorResponse.setErrMessage(errMessage);
        return restErrorResponse;

    }
}
