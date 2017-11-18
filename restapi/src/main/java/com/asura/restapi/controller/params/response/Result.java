package com.asura.restapi.controller.params.response;

/**
 * Created by Mario on 2017/9/26.
 * 统一返回类
 */
@SuppressWarnings("unused")
public class Result {
    public static final String SUCCESS_CODE = "1";
    public static final String ERROR_CODE = "0";
    public static final String NO_LOGIN = "-1";
    public static final String NEED_CAPTCHA = "2";
    public static final String NEED_SMS_CAPTCHA = "3";
    private String code;//状态码
    private String message;//信息
    private Object data;//具体数据
    
    public Result(Object data) {
        this.data = data;
        this.code = Result.SUCCESS_CODE;
        this.message = "请求成功";
    }
    public Result() {
        this.code = Result.SUCCESS_CODE;
        this.message = "请求成功";
    }
    public Result(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
