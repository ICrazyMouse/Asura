package com.asura.restapi.model;

/**
 * Created by lichuanshun on 2017/11/18.
 */

public class TaxUser {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    // 验证码
    String captcha;
    // 用户名
    String userName;
    // 密码
    String pwd;
    // 城市id
    String cityCode;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    // 唯一任务id
    String taskId;

}
