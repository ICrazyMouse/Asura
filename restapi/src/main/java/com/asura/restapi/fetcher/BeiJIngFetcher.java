package com.asura.restapi.fetcher;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.annotations.Fetcher;
import com.asura.restapi.api.IFetcher;
import com.asura.restapi.common.BaseFetcher;
import com.asura.restapi.common.LoginContext;
import com.asura.restapi.controller.params.response.Result;
import com.asura.restapi.model.TaxUser;
import com.asura.restapi.model.dto.TaskDto;
import com.asura.restapi.model.dto.TaxInfo;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lichuanshun on 2017/11/23.
 */
@Fetcher(code="110100")
public class BeiJIngFetcher extends BaseFetcher implements IFetcher {

    // 初始化接口
    protected static String INIT_URL = "https://gt3app9.tax861.gov.cn/Gt3GsWeb/gsmxwyNo/YhdlAction.action?code=init";

    // 刷新图片验证码
    protected static String REFRESH_CAPTCHA_URL = "https://gt3app9.tax861.gov.cn/Gt3GsWeb/RandomCode.action";

    // 登录
    protected static String LOGIN_URL = "https://gt3app9.tax861.gov.cn/Gt3GsWeb/gsmxwyNo/YhdlAction.action?code=login";
    // 查询列表
    protected static String QUERY_URL = "https://gt3app9.tax861.gov.cn/Gt3GsWeb/gsmxwyNo/GrnsxxcxAction.action?code=query";
    // 查询详情 2016-08之前的数据不可查
    protected static String DETAIL_URL = "https://gt3app9.tax861.gov.cn/Gt3GsWeb/gsmxwyNo/GrnsxxcxAction.action?code=queryMx&sbblx=BDA0610135&jylsh=&sdxm_dm=101060100";




    @Override
    public Result pageInit(TaxUser taxUser) {
        logger.info("beijing pageInit:start");
        long start = System.currentTimeMillis();
        Result result = new Result();
        //初始化
        BasicCookieStore cookieStore = new BasicCookieStore();
        LoginContext loginContext = createLoginContext(cookieStore);

        loginContext.setUri(INIT_URL);
        // 页面初始化
        String initResult = doGet(loginContext);

        // 初始化taskid
        String taskId = createTaskId();

        String captcha = refreshCaptcha(loginContext, taskId);

        // 缓存cookie
        boolean cookieSave = cacheLoginCookie(taskId, loginContext.getCookieStore());
        logger.info("cookieSave:" + cookieSave);

        // 返回数据
        result.setCode(Result.NEED_CAPTCHA);
        JSONObject data = new JSONObject();
        data.put("taskId", taskId);
        data.put("captcha",captcha);
        result.setData(data);
        long take = System.currentTimeMillis() - start;
        logger.info("beijing pageInit:end" + take);
        return result;
    }

    @Override
    public Result login(TaxUser taxUser) {
        logger.info("beijing login:start");
        String taskId = taxUser.getTaskId();
        long start = System.currentTimeMillis();
        Result result = new Result();
        //初始化
        BasicCookieStore cookieStore = (BasicCookieStore)getCachedLoginCookie(taskId);
        LoginContext loginContext = createLoginContext(cookieStore);

        Map<String, String> loginParams = new LinkedHashMap();
        loginParams.put("zjlx", taxUser.getIdType());
        loginParams.put("zzhm", taxUser.getIdnum());
        loginParams.put("xm", taxUser.getUserName());
        loginParams.put("password", taxUser.getPwd());
        loginParams.put("yzm", taxUser.getCaptcha());
        loginContext.setParams(loginParams);
        loginContext.setUri(LOGIN_URL);
        String loginResult = doPost(loginContext);
        Document docment = Jsoup.parse(loginResult);

        logger.info(docment.html());
        Elements message = docment.getElementsByAttributeValue("name","message");

        if (message != null && message.size() > 0){
            String errorMsg = message.get(0).attr("value");
            result.setCode(Result.NEED_CAPTCHA);
            result.setMessage(errorMsg);
            //
            JSONObject data = new JSONObject();
            data.put("taskId",taskId);
            data.put("captcha",refreshCaptcha(loginContext,taskId));
            result.setData(data);
            cacheLoginCookie(taskId, loginContext.getCookieStore());
            return result;
        } else {

            // 查询当前时间向前十年的数据
            Calendar cl = Calendar.getInstance();
            cl.set(Calendar.MONTH, cl.get(Calendar.MONTH) + 1);

            int nowYear = cl.get(Calendar.YEAR);
            int nowMonth = cl.get(Calendar.MONTH);
            String search_from_year = (nowYear - 10) + "";
            //

            Map<String, String> queryParams = new LinkedHashMap();
            queryParams.put("tijiao", "grsbxxcx");
            queryParams.put("actionType", "query");
            queryParams.put("index", "");
            queryParams.put("skssksrqN", search_from_year);
            queryParams.put("skssksrqY", nowMonth + "");
            queryParams.put("skssjsrqN", nowYear + "");
            queryParams.put("skssjsrqY", nowMonth + "");
            queryParams.put("sbbmc", "");
            queryParams.put("code", "query");
            loginContext.setParams(queryParams);

            //
            Map<String, String> headers = loginContext.getRequestHeaders();
            headers.clear();
            headers.put("Host", "gt3app9.tax861.gov.cn");
            headers.put("Origin", "https://gt3app9.tax861.gov.cn");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

            headers.put("Referer", "https://gt3app9.tax861.gov.cn/Gt3GsWeb/gsmxwyNo/GrnsxxcxAction.action?code=query");

            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            headers.put("Upgrade-Insecure-Requests", "1");
            headers.put("Pragma", "no-cache");
            headers.put("Cache-Control", "no-cache");
            loginContext.setUri(QUERY_URL);
            String queryResult = doPost(loginContext);
            Document doc = Jsoup.parse(queryResult);
            Element dyJson = doc.getElementById("dyJson");
            result.setMessage("登录成功,正在解析");

            //
            saveTaskId(taxUser);
            if (dyJson != null) {
                String taxData = dyJson.text();
                JSONArray dataArr = JSONObject.parseArray(taxData);
                TaskDto taskInfo = queryTaskByTaskId(taskId);
                for (Object tempObj : dataArr){
                    JSONObject data = (JSONObject)tempObj;
                    TaxInfo taxInfo = transformData(data);
                    taxInfo.setSource(taskInfo.getSource());
                    taxInfo.setForeign_id(taskInfo.getId());
                    //保存入库
                    saveTaxInfo(taxInfo);
                }

//                dataArr.forEach(data -> {
//                    JSONObject tempData = (JSONObject) data;
//                    TaxInfo taxInfo = transformData(tempData);
//                    taxInfo.setSource(taskInfo.getSource());
//                    taxInfo.setForeign_id(taskInfo.getId());
//                    //保存入库
//                    saveTaxInfo(taxInfo);
//                });
            } else {

            }
            clearMemcache(taskId);
        }

        long take = System.currentTimeMillis() - start;
        logger.info("beijing login:end" + take);
        return result;
    }




    @Override
    public Result refreshCaptcha(TaxUser taxUser) {

        Result result = new Result();
        String taskId = taxUser.getTaskId();
        logger.info("beijing login:start" + taskId);
        long start = System.currentTimeMillis();

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
        logger.info(taskId + "beijing login:end" + take);
        return result;
    }

    @Override
    public Result refreshSms(TaxUser taxUser) {
        return null;
    }


    @Override
    public void logout(JSONObject params) throws Exception {
        logger.info("logout:&&&&&&&&&&&&&&&&&&&&&&&&&");
    }

    @Override
    public TaxUser loginAndParseInfo(JSONObject params) throws Exception {
        logger.info("loginAndParseInfo:&&&&&&&&&&&&&&&&&&&&&&&&&");
        return null;
    }

    /**
     *
     * @return
     */
    private String refreshCaptcha(LoginContext loginContext, String taskId){
        Map<String, String> headers = loginContext.getRequestHeaders();
        headers.put("Referer", INIT_URL);
        String yzmUrl = REFRESH_CAPTCHA_URL ;
        loginContext.setUri(yzmUrl);
        return doYzm(taskId, yzmUrl,loginContext);
    }

    /**
     * 格式转换
     * @param data
     * @return
    "jsjdm": "",
    "jylsh": "",
    "kjywrmc": "北京兄弟创赢信息技术有限公司",
    "rtkrq": "2015-01-08 00:00:00.0",
    "sbblx": "BDA0610135",
    "sbrq": "",
    "sdxmdm": "101060100",
    "skssq": "2014-12-01",
    "skssz": "2014-12-31",
    "sl": 0,// 税率
    "sre": 6337.49,
    "swjgdm": "21101084100",
    "ykjse": 178.75
     */
    private TaxInfo transformData(JSONObject data){
        TaxInfo taxInfo = new TaxInfo();
        String periodStart = data.getString("skssq");
        taxInfo.setPeriod_start(periodStart);
        taxInfo.setPeriod_end(data.getString("skssz"));
        // 缴税周期
        if (!StringUtils.isEmpty(periodStart)){
            String period = periodStart.replace("-","").substring(0, periodStart.length()-2);
            taxInfo.setPeriod(period);
        }
        //扣缴义务人名称 kjywrmc
        taxInfo.setTax_unit_name(data.getString("kjywrmc"));
        // 缴税日期rtkrq
        taxInfo.setTax_date(data.getString("rtkrq"));
        // dzlbzdsdm
        taxInfo.setDzlbzdsdm(data.getString("sdxmdm"));
        // 申报日期sbrq
        taxInfo.setTax_submit_date(data.getString("sbrq"));

        // 项目代码sjsdxmdm
        taxInfo.setSjsdxmdm(data.getString("sdxmdm"));
        // 收入额sre
        taxInfo.setIncome(data.getString("sre"));
        // 实际金额ykjse
        taxInfo.setReal_amount(data.getString("ykjse"));
        return taxInfo;
    }
}
