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
    private String captcha;
    // 用户名
    private String userName;
    // 密码
    private String pwd;
    // 城市id
    private String cityCode;
    // 唯一任务id
    private String taskId;
    // 证件类型
    private String idType;
    // 来源渠道
    private String soure;

}
