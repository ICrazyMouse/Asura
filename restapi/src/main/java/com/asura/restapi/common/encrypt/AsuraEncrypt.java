package com.asura.restapi.common.encrypt;


import java.io.UnsupportedEncodingException;

/**
 * @author zhangnaiqi
 * @Description:
 * @ClassName:
 * @date since 20140528 1:58:20
 */
public final class AsuraEncrypt {


    public static final String ENCRYPT_KEY = "9188123123123345";
    public static final String ENCRYPT_UTF8 ="utf-8";
    public AsuraEncrypt() {
    }

    /**
     * @param value
     * @return
     */
    public static String encryptStr(String value) {
        try {
            byte[] temp = AESUtil.encrypt(ENCRYPT_KEY,
                    ENCRYPT_KEY, value.getBytes(ENCRYPT_UTF8));
            return Base64Tax.encode(temp, ENCRYPT_UTF8).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param value
     * @return
     */
    public static String dencryptStr(String value) {
        try {
            byte[] temp = AESUtil.decrypt(ENCRYPT_KEY,
                    ENCRYPT_KEY, Base64Tax.decode(value.getBytes(ENCRYPT_UTF8)));
            return new String(temp, ENCRYPT_UTF8).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}