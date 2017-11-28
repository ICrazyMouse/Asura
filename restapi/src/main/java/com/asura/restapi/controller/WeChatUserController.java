package com.asura.restapi.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.common.BaseFetcher;
import com.asura.restapi.common.LoginContext;
import com.asura.restapi.common.MemcacheClient;
import com.asura.restapi.common.encrypt.WeChatAESUtil;
import com.asura.restapi.controller.params.response.Result;
import com.asura.restapi.model.TaxUser;
import com.asura.restapi.model.WeChatUser;
import com.asura.restapi.model.dto.TaxInfo;
import com.asura.restapi.service.TaxInfoService;
import com.asura.restapi.service.WeChatUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Created by lichuanshun on 2017/11/25.
 */
@Api(value = "/asura/tax/wechat/user", description = "微信用户相关")
@RestController
@RequestMapping(value = "/asura/tax/wechat/user")
public class WeChatUserController extends BaseFetcher {
    // 微信公众号appid
    private static String WECHAT_APPID = "wxcd073f7d5ded6530";
    // 微信公众号AppSecret
    private static String WECHAT_APPSECRET = "88057f2cdf60c6857a6177c9474fd13d";

    @Autowired
    protected WeChatUserService weChatUserService;
    @Autowired
    protected TaxInfoService taxInfoService;
    //缓存
    @Autowired
    protected MemcacheClient memcacheClient;


    @RequestMapping(value = "/taxinfo", method = RequestMethod.POST)
    @ApiImplicitParam(name = "weChatUser", value = "用户详细实体user", required = true, dataType = "WeChatUser")
    public Result queryUserTaxInfo(@RequestBody WeChatUser weChatUser){
        Result result = new Result();
        String uid = weChatUser.getUid();
        logger.info("uid:" + uid);
        if (StringUtils.isEmpty(uid)){
            result.setCode(Result.ERROR_CODE);
            result.setMessage("缺少参数");
            return result;

        }
        //
        List<TaxInfo> taxInfo = taxInfoService.queryTaxInfoByUid(uid);

        List<TaxInfo> taxUnitInfo =  taxInfoService.queryTaxUnitAndMoenyByUid(uid);

        if (taxUnitInfo == null || taxUnitInfo.size() <1){
            result.setCode(Result.ERROR_CODE);
            result.setMessage("暂无数据");
            return result;
        }
        JSONObject data = new JSONObject();
        data.put("unit", taxUnitInfo);
        data.put("tax", taxInfo);
        result.setData(data);
        return result;
    }


    @RequestMapping(value = "/userinfo", method = RequestMethod.POST)
    @ApiImplicitParam(name = "weChatUser", value = "用户详细实体user", required = true, dataType = "WeChatUser")
    public Result queryUserInfo(@RequestBody WeChatUser weChatUser){
        Result result = new Result();
        String uid = weChatUser.getUid();
        logger.info("uid:" + uid);
        if (StringUtils.isEmpty(uid)){
            result.setCode(Result.ERROR_CODE);
            result.setMessage("缺少参数");
            return result;
        }
        WeChatUser user = weChatUserService.queryWeChatUser(uid);
        result.setData(user);
        return result;
    }
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiImplicitParam(name = "weChatUser", value = "用户详细实体user", required = true, dataType = "WeChatUser")
    public Result queryUserOpenId(@RequestBody WeChatUser weChatUser){
        Result result = new Result();
        String code = weChatUser.getCode();
        logger.info("code:" + code);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WECHAT_APPID + "&secret=" + WECHAT_APPSECRET
                +"&grant_type=authorization_code&js_code=" +code;
        //初始化
        BasicCookieStore cookieStore = new BasicCookieStore();
        LoginContext loginContext = createLoginContext(cookieStore);
        loginContext.setUri(url);
        String resultStr = doPost(loginContext);
        System.out.println(result);
        JSONObject opensult = JSONObject.parseObject(resultStr);
        String openId = opensult.getString("openid");
        String access_token = opensult.getString("session_key");

        String userInfo = decryptUserData(weChatUser.getEncryptedData(),access_token,weChatUser.getIv());
        logger.info("userInfo:" + userInfo);
        WeChatUser userInfoTemp = JSONObject.parseObject(userInfo,WeChatUser.class);


        userInfoTemp.setSource(weChatUser.getSource());
        //
        WeChatUser check = weChatUserService.checkWeChatUserOpenId(openId, weChatUser.getSource());
        String uid = "";
        if (check != null && !StringUtils.isEmpty(check.getUid())){
            logger.info("uid:" + check.getUid());
            uid = check.getUid();
        } else {
            uid = UUID.randomUUID().toString();
            userInfoTemp.setUid(uid);
            weChatUserService.saveWeChatUser(userInfoTemp);
        }
        logger.info("getOpenId:" + userInfoTemp.getOpenId());
        result.setData(uid);
        return result;
    }


    /**
     * 解密用户数据
     * @param encryptedData
     * @param session_key
     * @param iv
     * @return
     */
    private String decryptUserData(String encryptedData, String session_key,String iv){
        String userData = "";
        try {
            byte[] resultByte = WeChatAESUtil.instance.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(session_key), Base64.decodeBase64(iv));
            userData = new String(resultByte, "UTF-8");
        } catch (Exception e) {
            logger.error("decryptUserData:"  + e.getMessage(), e);
        }
        return userData;

    }

    @Override
    public void logout(JSONObject params) throws Exception {

    }

    @Override
    public TaxUser loginAndParseInfo(JSONObject params) throws Exception {
        return null;
    }
}
