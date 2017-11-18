package com.asura.restapi.common;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lichuanshun on 2017/11/3.
 */
public interface BaseService {

    Map<String, Boolean> processingHolder = new ConcurrentHashMap<>();

    default Boolean processing(String uid){
        return processingHolder.containsKey(uid);
    }

    default void process(String uid){
        processingHolder.put(uid, true);
    }

    default void processed(String uid){
        processingHolder.remove(uid);
    }
    default String readHttpContent(HttpEntity entity, String encode) throws Exception {
        StringBuilder buffer = new StringBuilder();
        InputStream in = null;
        try {
            in = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return buffer.toString();
    }
    default void closeAndReturnHttpConnection(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
