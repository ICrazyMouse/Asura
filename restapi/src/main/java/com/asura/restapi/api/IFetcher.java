package com.asura.restapi.api;

import com.asura.restapi.controller.params.response.Result;
import com.asura.restapi.model.TaxUser;

/**
 * Created by Mario on 2017/11/13 0013.
 * fetch 接口
 */
public interface IFetcher {


    /**
     * 页面初始化
     * @param taxUser
     * @return
     */
    Result pageInit(TaxUser taxUser);


    /**
     * 用户登录
     * @param taxUser
     * @return
     */
    Result login(TaxUser taxUser);


    /**
     * 刷新验证码
     * @param taxUser
     * @return
     */
    Result refreshCaptcha(TaxUser taxUser);


    /**
     * 刷新短信验证码
     * @param taxUser
     * @return
     */
    Result refreshSms(TaxUser taxUser);




}
