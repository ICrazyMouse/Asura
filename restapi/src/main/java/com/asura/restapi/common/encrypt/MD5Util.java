package com.asura.restapi.common.encrypt;

import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by Linxingyu on 2017/9/5.
 */
public class MD5Util {
    public static char[] hexChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public MD5Util() {
    }

    public static String compute(String paramString) throws Exception {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte1 = new byte[arrayOfChar.length];

        for (int arrayOfByte2 = 0; arrayOfByte2 < arrayOfChar.length; ++arrayOfByte2) {
            arrayOfByte1[arrayOfByte2] = (byte) arrayOfChar[arrayOfByte2];
        }

        byte[] var8 = localMessageDigest.digest(arrayOfByte1);
        StringBuffer localStringBuffer = new StringBuffer();

        for (int j = 0; j < var8.length; ++j) {
            int k = var8[j] & 255;
            if (k < 16) {
                localStringBuffer.append("0");
            }

            localStringBuffer.append(Integer.toHexString(k));
        }

        return localStringBuffer.toString();
    }

    public static String getHash(String paramString) throws Exception {
        int i = 0;
        FileInputStream localFileInputStream = new FileInputStream(paramString);
        byte[] arrayOfByte = new byte[1024];
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");

        int var6;
        for (boolean j = false; (var6 = localFileInputStream.read(arrayOfByte)) > 0; ++i) {
            localMessageDigest.update(arrayOfByte, 0, var6);
        }

        localFileInputStream.close();
        return toHexString(localMessageDigest.digest());
    }

    public static String toHexString(byte[] paramArrayOfByte) {
        StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);

        for (int i = 0; i < paramArrayOfByte.length; ++i) {
            localStringBuffer.append(hexChar[(paramArrayOfByte[i] & 240) >>> 4]);
            localStringBuffer.append(hexChar[paramArrayOfByte[i] & 15]);
        }

        return localStringBuffer.toString();
    }

}
