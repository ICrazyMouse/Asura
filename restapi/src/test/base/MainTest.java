package base;

import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.common.BaseFetcher;
import com.asura.restapi.common.LoginContext;
import com.asura.restapi.common.encrypt.WeChatAESUtil;
import com.asura.restapi.model.TaxUser;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * Created by lichuanshun on 2017/11/20.
 */
public class MainTest extends BaseFetcher {

    public static void main(String[] args) {
//        String test = "lcs9188";
//
//        String encryptStr = AsuraEncrypt.encryptStr(test);
//
//        System.out.println(encryptStr);
//
//        System.out.println(AsuraEncrypt.dencryptStr(encryptStr));
//
//        JSONObject params = new JSONObject();
//        params.put("cityCode", "310100");
//        params.put("userName", "17612188926");
//        params.put("pwd", "love1990");
//        params.put("taskId","1");
//        params.put("captcha","1234");
//        System.out.println(JSONObject.toJSONString(params));
//        MainTest mainTest = new MainTest();
//        mainTest.encryptWxData();

        byte[] result = WeChatAESUtil.instance.encrypt(Base64.decodeBase64("love1990"),

                Base64.decodeBase64("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEKu2Fc233FMyxtgQZSS6+b/7rIquYbTWfJM5kOkJDVDUe9UD8WSgj3hpXumLxiK2eUJFutCYRpch4GqplPVejYz/LRb6/Zapu+LrVMbmE0aU8AYfs0uemkVUHkMVnJWi3oUOVUMf3AroZ4UJctwawl2b98suKOwdjTk7Lywb6kwIDAQAB"));
        System.out.println(result);
    }


    /**
     *
     */
    private void encryptWxData(){
        String appId =  "wxcd073f7d5ded6530";
        String secret = "88057f2cdf60c6857a6177c9474fd13d";
        String code = "011K1Wi80zpeVH1Vntk80OrSi80K1WiD";

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" +secret+"&grant_type=authorization_code&js_code=" +code;
        //初始化
        BasicCookieStore cookieStore = new BasicCookieStore();
        LoginContext loginContext = createLoginContext(cookieStore);
        loginContext.setUri(url);
        //o7-YF0WHXqwicZEfyfXfiDmgiLE0
        String result = doPost(loginContext);
        System.out.println(result);
        JSONObject opensult = JSONObject.parseObject(result);
        String openId = opensult.getString("openid");
        String access_token = opensult.getString("session_key");

//        String urlForToken = "https://api.weixin.qq.com/cgi-bin/token";
//        Map<String,String > params = new HashedMap();
//        params.put("appid", appId);
//        params.put("secret", secret);
//        params.put("grant_type", "client-credntial");
//        loginContext.setParams(params);
//        loginContext.setUri(urlForToken);
//        String token = doPost(loginContext);
//        System.out.println("token:" + token);
//
//
//        String result001 = doPost(loginContext);
//        String url222 = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + secret + "&code=" + code
//                + "&grant_type=authorization_code";
//
//        loginContext.setUri(url222);
//        String token = doGet(loginContext);
//        System.out.println(token);
//
//        String user= "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+access_token + " &openid="+openId +  " &lang=zh_CN";
////        String url2 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openId;
//        loginContext.setUri(user);
//        String result2 = doGet(loginContext);
//        System.out.println(result2);

//        try {
//            byte[] resultByte = AESUtil.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(session_key), Base64.decodeBase64(iv));
//            if(null != resultByte && resultByte.length > 0){
//                String userInfo = new String(resultByte, "UTF-8");
//                System.out.println(userInfo);
//                JSONObject json = JSONObject.fromObject(userInfo); //将字符串{“id”：1}
//                renderJson(json);
//            }
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void logout(JSONObject params) throws Exception {

    }

    @Override
    public TaxUser loginAndParseInfo(JSONObject params) throws Exception {
        return null;
    }


}
