package com.asura.restapi.fetcher;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.annotations.Fetcher;
import com.asura.restapi.api.IFetcher;
import com.asura.restapi.common.BaseFetcher;
import com.asura.restapi.common.LoginContext;
import com.asura.restapi.controller.params.response.Result;
import com.asura.restapi.model.dto.TaxInfo;
import com.asura.restapi.model.TaxUser;
import com.asura.restapi.model.dto.TaskDto;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lichuanshun on 2017/11/18.
 *
 * 上海个税登录相关
 */
@Fetcher(code="310100")
public class ShangHaiFetcher extends BaseFetcher implements IFetcher{

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
        String taskId = creatTaskId(taxUser);
        //
        String captcha = refreshCaptcha(loginContext,taskId);

        logger.info("data:image/png;base64," + captcha);

        logger.info(taskId);

        // 缓存cookie
        boolean cookieSave = cacheLoginCookie(taskId, loginContext.getCookieStore());
        logger.info("cookieSave:" + cookieSave);

        // 缓存公钥
        boolean keySave = cacheRsaPublicKey(taskId, rsaPublicKey);
        logger.info("keySave:" + keySave);
        // 返回数据
        result.setCode(Result.NEED_CAPTCHA);
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


        String rsaPublicKey = (String)getCacheRsaPublicKey(taskId);
        //初始化
        BasicCookieStore cookieStore = (BasicCookieStore)getCachedLoginCookie(taskId);
        LoginContext loginContext = createLoginContext(cookieStore);
        // 验证验证码
        String checkYzmUrlR = CHECK_CATTCHA_URL + taxUser.getCaptcha();
        loginContext.setUri(checkYzmUrlR);
        String checkResult = doPost(loginContext);
        logger.info(taskId + "验证验证码结果**************"+ checkResult);

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

            // 更新登录cookie
            cacheLoginCookie(taskId,loginContext.getCookieStore());
            return result;
        }


        // 加密密码
        String keyEncoded = rsaPublicKey;
        try {
            keyEncoded =  URLEncoder.encode(rsaPublicKey,"utf-8");
        }catch (Exception e){
            logger.error(taskId + ":" + e.getMessage(), e);
        }
        String url = PWD_RAS_URL.replace("itemval",taxUser.getPwd())
                .replace("keyval", keyEncoded);
        logger.info(taskId + "PWD_RAS_URL:"  + url);

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

        logger.info(taskId + "开始登录************");

        String loginResult = httpPostPayload(loginContext,JSONObject.toJSONString(loginParams) );
        logger.info(taskId + "loginResult:" + loginResult);

        JSONObject login = JSONObject.parseObject(loginResult);

        if ("SUCCESS".equals(login.getString("type")) ){
            //登录成功
            setTaskStatusParsing(taskId);
            logger.info(taskId + "登录成功********************");
        } else {
            result.setCode(Result.ERROR_CODE);
            result.setMessage(login.getString("content"));

            cacheLoginCookie(taskId,loginContext.getCookieStore());
            return result;
        }
        long take = System.currentTimeMillis() - start;
        logger.info(taskId + "shanghai login:end" + take);
        result.setMessage("登录成功,正在解析");
        //
        String parseResult = ParseTaxInfo(loginContext);

        logger.info(taskId + ":parseResult:" + parseResult);
        //
        JSONObject taxData = JSONObject.parseObject(parseResult);

        if ("SUCCESS".equals(taxData.getString("type"))){
            //
            TaskDto taskInfo = queryTaskByTaskId(taskId);
            JSONArray dataArr = taxData.getJSONArray("data");
            dataArr.forEach(data -> {
                JSONObject tempData = (JSONObject)data;
                TaxInfo taxInfo = transformData(tempData);
                taxInfo.setForeign_id(taskInfo.getId());
                taxInfo.setSource(taskInfo.getSource());
                //保存入库
                saveTaxInfo(taxInfo);
            });

            clearMemcache(taskId);

        } else {

        }
        return result;
    }

    @Override
    public Result refreshCaptcha(TaxUser taxUser) {
        Result result = new Result();
        String taskId = taxUser.getTaskId();
        logger.info("shanghai login:start" + taskId);
        long start = System.currentTimeMillis();
        System.setProperty ("jsse.enableSNIExtension", "false");

        //初始化
        BasicCookieStore cookieStore = (BasicCookieStore)getCachedLoginCookie(taskId);
        LoginContext loginContext = createLoginContext(cookieStore);

        // 获取验证码
        String captcha = refreshCaptcha(loginContext, taskId);

        // 刷新缓存中的验证码
        cacheLoginCookie(taskId, loginContext.getCookieStore());
        JSONObject data = new JSONObject();
        data.put("taskId", taskId);
        data.put("captcha", captcha);
        result.setData(data);
        long take = System.currentTimeMillis() - start;
        logger.info(taskId + "shanghai login:end" + take);
        return result;
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


    /**
     * 初始化任务
     * @param taxUser
     * @return
     */
    private String creatTaskId(TaxUser taxUser){
        TaskDto taskDto = new TaskDto();
        taskDto.setCity_code(taxUser.getCityCode());
        taskDto.setUser_name(taxUser.getUserName());
        taskDto.setPwd(taxUser.getPwd());
        taskDto.setSource(Optional.ofNullable(taxUser.getSoure()).orElse("self"));
        taskService.createTask(taskDto);
        logger.info("taskId:" + taskDto.getTask_id());
        return taskDto.getTask_id();
    }

    /**
     * 获得加密密码
     * @param url
     * @return
     */
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

    /**
     * 登录成功之后 解析个税信息的前置请求
     */
    private void PreActionForParseTaxInfo(LoginContext loginContext){
        Map<String, String> headers = loginContext.getRequestHeaders();

        // 进入主页
        String homeUrl = "https://gr.tax.sh.gov.cn/portals/web/biz/home";

        loginContext.setUri(homeUrl);
        headers.remove("Origin");
        String home = doPost(loginContext);
//        logger.debug("home:" + home);

        headers.put("Referer", "https://gr.tax.sh.gov.cn/portals/web/biz/home");
        headers.put("Origin", "https://gr.tax.sh.gov.cn");

        //https://gr.tax.sh.gov.cn/portals/web/biz/getMyMsgCount

        loginContext.setUri("https://gr.tax.sh.gov.cn/portals/web/biz/getMyMsgCount");
        String getMyMsgCount = doPost(loginContext);
        logger.info("getMyMsgCount:" + getMyMsgCount);


        // https://gr.tax.sh.gov.cn/portals/web/biz/getMySysMsgs
        loginContext.setUri("https://gr.tax.sh.gov.cn/portals/web/biz/getMySysMsgs");
        String getMySysMsgs = doPost(loginContext);
        logger.info("getMySysMsgs:" + getMySysMsgs);

        //https://gr.tax.sh.gov.cn/portals/web/biz/checkMb
        loginContext.setUri("https://gr.tax.sh.gov.cn/portals/web/biz/checkMb");
        String checkMb = doPost(loginContext);
        logger.info("checkMb:" + checkMb);

        //https://gr.tax.sh.gov.cn/portals/web/biz/checkMmczzt
        loginContext.setUri("https://gr.tax.sh.gov.cn/portals/web/biz/checkMmczzt");
        String checkMmczzt = doPost(loginContext);
        logger.info("checkMmczzt:" + checkMmczzt);

        logger.info("个税清单申请:start---------------------");

        // 个税清单申请
        // 主页
        headers.clear();
        headers.put("Accept-Encoding","gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        headers.put("Upgrade-Insecure-Requests","1");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Host","gr.tax.sh.gov.cn");
        loginContext.setUri("https://gr.tax.sh.gov.cn/wsz-ww-web/web/shanghai/taxInfo");

        String selfTaxPage = doGet(loginContext);

//        logger.debug(selfTaxPage);

        // https://gr.tax.sh.gov.cn/wsz-ww-web/web/help/rdwt?subMkDm=001&_=1509823382469
        logger.info("subMkDm:start **********");
        headers.put("Referer","https://gr.tax.sh.gov.cn/wsz-ww-web/web/shanghai/taxInfo");
        loginContext.setUri("https://gr.tax.sh.gov.cn/wsz-ww-web/web/help/rdwt?subMkDm=001&_=" + System.currentTimeMillis());
        String rdwt = doGet(loginContext);
        logger.info(rdwt);

    }

    /**
     * 解析当前日期前十年的个税信息
     * @param loginContext
     */
    private String ParseTaxInfo(LoginContext loginContext){
        //
        PreActionForParseTaxInfo(loginContext);
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.MONTH,cl.get(Calendar.MONTH) + 1);

        int nowYear = cl.get(Calendar.YEAR);
        int nowMonth = cl.get(Calendar.MONTH);
        String search_from = (nowYear - 10) + "-" + nowMonth + "-01";
        String search_end = nowYear + "-" + nowMonth + "-01";

        String querySelfTax = "https://gr.tax.sh.gov.cn/wsz-ww-web/web/shanghai/taxInfo/search?" +
                "skssqq=" + search_from + "&skssqz=" + search_end + "&_=" + System.currentTimeMillis();

        loginContext.setUri(querySelfTax);

        loginContext.getRequestHeaders().put("Refer", "https://gr.tax.sh.gov.cn/wsz-ww-web/web/shanghai/taxInfo");
        return doGet(loginContext);

    }
    /**
     * 登录成功之后 解析用户信息
     * https://gr.tax.sh.gov.cn/portals/web/biz/personalInfo/personalInfoPage
     * https://gr.tax.sh.gov.cn/wssb-app-ww-web/web/jcxxb/grxx
     */
    private void ParseTaxUserInfo(){
        // TODO: 2017/11/21
    }


    /**
     * 格式转换
     * @param data
     * @return
     *  "nsxm" : "工资薪金所得",
    "rtkse" : 1724.28,
    "skssqq" : "2017-09-01",
    "skssqz" : "2017-09-30",
    "zsjg" : "上海市徐汇区税务局第二十一税务所",
    "kjywr" : "上海彩亿信息技术有限公司",
    "kjywrdm" : "10013101003000686666",
    "rtkrq" : "2017-10-23",
    "sjsdxmdm" : "0100",
    "sre" : 15100.0,
    "ynssde" : 0.0,
    "kjsb" : true,
    "orderBy" : 0,
    "sdxmDm" : "0101",
    "zsjgdm" : "13101043900",
    "dzlbzdsdm" : "BDA0610135",
    "sbrq" : "2017-10-19",
    "sdxmmc" : "工资薪金所得",
    "sdxmdm" : "0100",
    "sjje" : 1724.28,
    "skssjg" : "上海市徐汇区税务局",
    "kjywrdjxh" : "10013101003000686666",
    "kjywrmc" : "上海彩亿信息技术有限公司",
    "rkrq" : "2017-10-23",
    "dzsph" : "320171020000205113",
    "nsrsbh" : "91310104574100764P",
    "nsrxm" : null,
    "sfzjhm" : null,
    "sfzjlxdm" : null,
    "sfzjlxmc" : null,
    "szMc" : null,
    "szDm" : null
     */
    private TaxInfo transformData(JSONObject data){
        TaxInfo taxInfo = new TaxInfo();
        //纳税项目nsxm
        taxInfo.setItem(data.getString("nsxm"));
        // 缴税金额rtkse
        taxInfo.setTax_money(data.getString("rtkse"));
        //缴税周期开始日skssqq
        String periodStart = data.getString("skssqq");
        taxInfo.setPeriod_start(periodStart);
        // 缴税周期
        if (!StringUtils.isEmpty(periodStart)){
            String period = periodStart.replace("-","").substring(0, periodStart.length()-2);
            taxInfo.setPeriod(period);
        }
        // 征收机关zsjg
        taxInfo.setLevying_department(data.getString("zsjg"));
        // 缴税日期rtkrq
        taxInfo.setTax_date(data.getString("rtkrq"));
        // 缴税义务人 代缴一般为公司kjywr
        taxInfo.setTax_unit(data.getString("kjywr"));
        // 缴税义务人代码kjywrdm
        taxInfo.setTax_unit_code(data.getString("kjywrdm"));
        // 缴税周期结束日skssqz
        taxInfo.setPeriod_end(data.getString("skssqz"));
        // 收入额sre
        taxInfo.setIncome(data.getString("sre"));
        // 纳税人识别号nsrsbh
        taxInfo.setTax_unit_code(data.getString("nsrsbh"));
        // 征收机关代码zsjgdm
        taxInfo.setLeaving_department_code(data.getString("zsjgdm"));
        // 实际金额sjje
        taxInfo.setReal_amount(data.getString("sjje"));
        // 税款收缴机构skssjg
        taxInfo.setParent_department(data.getString("skssjg"));
        // 扣缴义务人kjywrdjxh
        taxInfo.setTax_unit_djxh(data.getString("kjywrdjxh"));
        //扣缴义务人名称 kjywrmc
        taxInfo.setTax_unit_name(data.getString("kjywrmc"));
        // 入库日期rkrq
        taxInfo.setSave_date(data.getString("rkrq"));
        // 纳税人姓名nsrxm
        taxInfo.setUser_name(data.getString("nsrxm"));
        // 身份证件号码sfzjhm
        taxInfo.setId_card_num(data.getString("sfzjhm"));
        // 身份证件类型代码sfzjlxdm
        taxInfo.setId_card_type_code(data.getString("sfzjlxdm"));
        // 身份证件类型名称sfzjlxmc
        taxInfo.setId_card_type(data.getString("sfzjlxmc"));
        // 项目代码sjsdxmdm
        taxInfo.setSjsdxmdm(data.getString("sjsdxmdm"));
        // ynssde
        taxInfo.setYnssde(data.getString("ynssde"));
        // 扣缴上报kjsb
        taxInfo.setTax_submit(data.getString("kjsb"));
        //项目名称sdxmDm
        taxInfo.setSdxmdm(data.getString("sdxmDm"));
        // dzlbzdsdm
        taxInfo.setDzlbzdsdm(data.getString("dzlbzdsdm"));
        // 上报日期sbrq
        taxInfo.setTax_submit_date(data.getString("sbrq"));
        // sdxmmc
        taxInfo.setSdxmmc(data.getString("sdxmmc"));
        // dzsph
        taxInfo.setDzsph(data.getString("dzsph"));
        // szmc
        taxInfo.setSzmc(data.getString("szMc"));
        // szdm
        taxInfo.setSzdm(data.getString("szDm"));

        return taxInfo;
    }
}
