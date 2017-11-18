package com.asura.restapi.fetcher;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.annotations.Fetcher;
import com.asura.restapi.api.IFetcher;
import com.asura.restapi.common.AbstractHttpService;
import com.asura.restapi.common.LoginContext;
import com.asura.restapi.common.MemcacheClient;
import com.asura.restapi.controller.params.response.Result;
import com.asura.restapi.model.TaxUser;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lichuanshun on 2017/11/18.
 *
 * 上海个税登录&注册相关
 */
@Fetcher(code="310100")
public class ShangHaiFetcher extends AbstractHttpService<TaxUser> implements IFetcher{

    // 页面初始化
    protected static final String PAGE_INIT_URL = "https://gr.tax.sh.gov.cn/portals/web/login";
    // 刷新图片验证码
    protected static final String REFRESH_CAPTCHA_URL = "https://gr.tax.sh.gov.cn/portals/web/captcha/refreshCaptcha?t=";
    // 验证验证码
    protected static final String CHECK_CATTCHA_URL = "https://gr.tax.sh.gov.cn/portals/web/captcha/validateCaptcha?captcha=";
    // 登录
    protected static final String LOGIN_URL = "https://gr.tax.sh.gov.cn//portals/web/oauth2/login";
    // 密码rsa加密
    protected static final String PWD_RAS_URL ="http://127.0.0.1:8080/shanghai/sh_rsa.html?item=itemval&key=keyval";


    //缓存
    protected MemcacheClient memcacheClient = MemcacheClient.getInstance();

    // 2 表示需要图片验证码


    @Override
    public void logout(JSONObject params) throws Exception {
        logger.info("logout--------");

    }

    @Override
    public TaxUser loginAndParseInfo(JSONObject params) throws Exception {
        logger.info("loginAndParseInfo--------");
        return null;
    }

    /**
     * 页面初始化  获取初始化cookies 同时获取图片验证码、rsa加密的公钥
     * @param taxUser
     * @return
     */
    @Override
    public Result pageInit(TaxUser taxUser) {
        logger.info("shanghai pageInit:start");
        long start = System.currentTimeMillis();
        Result result = new Result();
        //初始化
        BasicCookieStore cookieStore = new BasicCookieStore();
        LoginContext loginContext = createLoginContext(cookieStore);

        loginContext.setUri(PAGE_INIT_URL);
        // TODO: 2017/11/18 jdk版本不同造成ssl握手失败 服务器运行待验证 
        System.setProperty ("jsse.enableSNIExtension", "false");
        
        // 页面初始化
        String initResult = doGet(loginContext);

        // 获取rsa公钥
        String rsaPublicKey = getRsaPublicKeyFromInitPage(initResult);

        // 获取验证码

        // 初始化taskid
        String taskId = creatTaskId();
        //
        String captcha = refreshCaptcha(loginContext,taskId);

        logger.info("captcha:" + captcha);

        // 缓存cookie
        String redisCookieForShangHaiLogin = taskId + "shanghailogin";
//        redisTemplate.opsForValue().set(redisCookieForShangHaiLogin , loginContext.getCookieStore(), 30 * 60, TimeUnit.SECONDS);
        boolean cookieSave = memcacheClient.set(redisCookieForShangHaiLogin, loginContext.getCookieStore());
        logger.info("cookieSave:" + cookieSave);

        // 缓存公钥
        String redisKeyForShangHaiRsaKey = taskId + "shanghairsapublickey";
        boolean keySave = memcacheClient.set(redisKeyForShangHaiRsaKey, rsaPublicKey);
        logger.info("keySave:" + keySave);
        // 返回数据
        JSONObject data = new JSONObject();
        data.put("taskId", taskId);
        data.put("captcha",captcha);
        result.setData(data);
        long take = System.currentTimeMillis() - start;
        logger.info("shanghai pageInit:end" + take);
        return result;
    }

    /**
     * 登录
     * @param taxUser
     * @return
     */
    @Override
    public Result login(TaxUser taxUser) {
        String taskId = taxUser.getTaskId();
        logger.info("shanghai login:start" + taskId);
        long start = System.currentTimeMillis();
        Result result = new Result();
        System.setProperty ("jsse.enableSNIExtension", "false");

        String redisCookieForShangHaiLogin = taskId + "shanghailogin";
        String redisKeyForShangHaiRsaKey = taskId + "shanghairsapublickey";

        String rsaPublicKey = (String)memcacheClient.get(redisKeyForShangHaiRsaKey);
        //初始化
//        BasicCookieStore cookieStore = (BasicCookieStore)redisTemplate.opsForValue().get(redisCookieForShangHaiLogin);
        BasicCookieStore cookieStore = (BasicCookieStore)memcacheClient.get(redisCookieForShangHaiLogin);
        LoginContext loginContext = createLoginContext(cookieStore);
        // 验证验证码
        String checkYzmUrlR = CHECK_CATTCHA_URL + taxUser.getCaptcha();
        loginContext.setUri(checkYzmUrlR);
        String checkResult = doPost(loginContext);
        logger.info("验证验证码结果**************"+ checkResult);

        JSONObject captchaCheck = JSONObject.parseObject(checkResult);

        // 验证码登录失败
        if (!"SUCCESS".equals(captchaCheck.getString("type")) || !StringUtils.isEmpty(captchaCheck.getString("data"))){
            result.setCode(Result.NEED_CAPTCHA);
            result.setMessage(captchaCheck.getString("content"));
            JSONObject tempDate = new JSONObject();
            tempDate.put("taskId", taskId);
            tempDate.put("captcha", refreshCaptcha(loginContext,taskId));
            result.setData(tempDate);
            result.setMessage(captchaCheck.getString("data"));
            return result;
        }

        String url = PWD_RAS_URL.replace("itemval",taxUser.getPwd())
                .replace("keyval", URLEncoder.encode(rsaPublicKey));
        logger.info("PWD_RAS_URL:"  + url);
        String mm = getSignPwd(url);

        //

        Map<String, String> loginParams = new LinkedHashMap();
        loginParams.put("yhm", taxUser.getUserName());
        loginParams.put("idType","201");
        loginParams.put("idNumber","");

        loginParams.put("mm", mm);

        loginParams.put("authCode",taxUser.getCaptcha().toLowerCase());

        loginParams.put("redirect_uri","");

        loginParams.put("response_type","");

        loginParams.put("client_id","");

        loginParams.put("st", "");
        loginParams.put("sign", "");
        loginParams.put("dllx", "yhm");

        loginContext.setUri(LOGIN_URL);

        Map<String, String> headers = loginContext.getRequestHeaders();

        headers.put("Referer", "https://gr.tax.sh.gov.cn/portals/web/login");
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("Host","gr.tax.sh.gov.cn");
        headers.put("Origin","https://gr.tax.sh.gov.cn");
        headers.put("X-Requested-With","XMLHttpRequest");

        headers.put("Accept-Encodin","gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");

        logger.info("开始登录************");

        String loginResult = httpPostPayload(loginContext,JSONObject.toJSONString(loginParams) );
        logger.info("loginResult:" + loginResult);

        JSONObject login = JSONObject.parseObject(loginResult);

        if ("SUCCESS".equals(login.getString("type")) ){
            logger.info("登录成功********************");
        } else {
            result.setCode(Result.ERROR_CODE);
            result.setMessage(login.getString("content"));

            return result;
        }
        long take = System.currentTimeMillis() - start;
        logger.info("shanghai login:end" + take);
        result.setMessage("登录成功,正在解析:");
        return result;
    }

    @Override
    public Result refreshCaptcha(TaxUser taxUser) {
        return null;
    }

    @Override
    public Result refreshSms(TaxUser taxUser) {
        return null;
    }


    /**
     * 从初始化页面获取rsa加密公钥
     * @param initPageHtml
     * @return
     */
    private String getRsaPublicKeyFromInitPage(String initPageHtml){
        String rsaPublicKey = "";
        // 获取rsa加密公钥
        try {
            Document initHtml = Jsoup.parse(initPageHtml);
            rsaPublicKey = initHtml.getElementById("st").attr("data-param-rsapubkey");
            logger.info("rsaPublicKey:" + rsaPublicKey);
        } catch (Exception e) {
            logger.error("getRsaPublicKeyFromInitPage:error:" + e.getMessage(), e);
        }
        return rsaPublicKey;
    }


    /**
     *
     * @return
     */
    private String refreshCaptcha(LoginContext loginContext, String taskId){
        Map<String, String> headers = loginContext.getRequestHeaders();
        headers.put("Referer", PAGE_INIT_URL);
        String yzmUrl = REFRESH_CAPTCHA_URL + Math.random();
        loginContext.setUri(yzmUrl);
        return doYzm(taskId, yzmUrl,loginContext);
    }


    private String creatTaskId(){
        // TODO: 2017/11/19
        return "1";
    }

    private String getSignPwd(String url) {
        String signPwd="";
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getCookieManager().setCookiesEnabled(true);//开启cookie管理
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(30000);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        try {
            HtmlPage page = webClient.getPage(url);
            signPwd=page.getElementById("result").getAttribute("value");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if (webClient!=null) {
                webClient.close();
            }
        }
        return signPwd;
    }
}
