package com.asura.restapi.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lichuanshun on 2017/11/27.
 */
@Data
@Getter
@Setter
public class WeChatUser extends BaseBean{

    // 普通用户的标识，对当前开发者帐号唯一
    private String openId = "";
    // 微信昵称
    private String nickName = "";
    // 普通用户性别，1为男性，2为女性
    private String gender;

    //
    private String code = "";
    // 应用密钥AppSecret
    private String secret = "";
    // unionid
    private String unionid = "";

    // 微信头像
    private String avatarUrl = "";


    // 普通用户个人资料填写的省份
    private String province = "";
    // 普通用户个人资料填写的城市
    private String city = "";
    // 国家，如中国为CN
    private String country = "";
    // 微信昵称
    private String wxNickName = "";
    // 用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
    private String privilege = "";

    private String weChatUid;

    private String encryptedData;
    private String iv;
}
