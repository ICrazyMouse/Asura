package com.asura.restapi.controller.params.response;

/**
 * Created by Mario on 2017/9/26.
 * 统一返回类
 */
@SuppressWarnings("unused")
public class Result {
    public static final int SUCCESS_CODE = 1;
    public static final int ERROR_CODE = 0;
    public static final int NO_LOGIN = -1;

    private int code;//状态码
    private String message;//信息
    private Object data;//具体数据
    
    public Result(Object data) {
        this.data = data;
        this.code = Result.SUCCESS_CODE;
        this.message = "请求成功";
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
