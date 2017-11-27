package com.asura.restapi.service;

import com.asura.restapi.mapper.WeChatMapper;
import com.asura.restapi.model.WeChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lichuanshun on 2017/11/27.
 */
@Service
public class WeChatUserService {

    @Autowired
    WeChatMapper weChatMapper;

    public int saveWeChatUser(WeChatUser weChatUser){
        return weChatMapper.saveWeChatUser(weChatUser);
    }

    public WeChatUser queryWeChatUser(String uid){
        return weChatMapper.queryWeChatUser(uid);
    }

    public WeChatUser checkWeChatUserOpenId(String openId,String source){
        return weChatMapper.checkWeChatUserOpenId(openId,source);
    }
}
