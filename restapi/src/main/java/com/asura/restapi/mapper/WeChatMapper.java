package com.asura.restapi.mapper;

import com.asura.restapi.model.WeChatUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by lichuanshun on 2017/11/27.
 */
@Mapper
public interface WeChatMapper {


    int saveWeChatUser(WeChatUser weChatUser);

    WeChatUser queryWeChatUser(@Param("uid") String uid);

    WeChatUser checkWeChatUserOpenId(@Param("openId") String openId, @Param("source") String source);
}
