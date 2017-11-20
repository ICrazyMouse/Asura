package com.asura.restapi.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lichuanshun on 2017/11/18.
 */

@Data
@Getter
@Setter
public class TaxUser {
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
