package base;

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
    }
}
