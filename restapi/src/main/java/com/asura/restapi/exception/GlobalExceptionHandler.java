package com.asura.restapi.exception;

import com.asura.restapi.controller.params.response.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Mario on 2017/10/16 0016.
 * 统一异常处理
 */
@SuppressWarnings("unused")
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result jsonErrorHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        return new Result(Result.ERROR_CODE, "服务器异常", e.getMessage());
    }

}
