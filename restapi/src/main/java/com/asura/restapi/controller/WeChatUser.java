package com.asura.restapi.controller;

import com.asura.restapi.common.MemcacheClient;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lichuanshun on 2017/11/25.
 */
@Api(value = "/wechat/user", description = "微信用户相关")
@RestController
public class WeChatUser {

    //缓存
    @Autowired
    protected MemcacheClient memcacheClient;




}
