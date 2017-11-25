package base;

import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.common.encrypt.AsuraEncrypt;

/**
 * Created by lichuanshun on 2017/11/20.
 */
public class MainTest {

    public static void main(String[] args) {
        String test = "lcs9188";

        String encryptStr = AsuraEncrypt.encryptStr(test);

        System.out.println(encryptStr);

        System.out.println(AsuraEncrypt.dencryptStr(encryptStr));

        JSONObject params = new JSONObject();
        params.put("cityCode", "310100");
        params.put("userName", "17612188926");
        params.put("pwd", "love1990");
        params.put("taskId","1");
        params.put("captcha","1234");
        System.out.println(JSONObject.toJSONString(params));

    }


    /**
     *
     */
    private void encryptWxData(){

    }
}
