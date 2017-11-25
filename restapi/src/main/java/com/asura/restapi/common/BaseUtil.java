package com.asura.restapi.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by lichuanshun on 2017/11/1.
 */
@Component
public class BaseUtil {
    @Autowired
    private Environment environment;

    public static Environment env;

    @PostConstruct
    public void beforeInit() {
        env = environment;
    }


    public static String getString(String key){
        return env.getProperty(key) ;
    }
    public static Boolean getConfigBoolean(String key){
        return "ture".equals(env.getProperty("key"))?Boolean.TRUE:Boolean.FALSE ;
    }

    public static Integer getConfigInteger(String key){
        return Integer.valueOf(env.getProperty(key)) ;
    }
}
