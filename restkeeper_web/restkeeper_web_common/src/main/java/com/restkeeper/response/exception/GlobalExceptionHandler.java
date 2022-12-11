package com.restkeeper.response.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常拦截
 *
 * @author MORRIS --> Java
 * @date 2022-12-10 22:07:03
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object Exception(Exception ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage());
        return response;
    }

}
