package com.asura.restapi.controller;

import com.asura.restapi.annotations.Fetcher;
import com.asura.restapi.api.IFetcher;
import com.asura.restapi.controller.params.response.Result;
import com.asura.restapi.model.TaxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario on 2017/11/13 0013.
 * Fetch Controller
 */
@Api(description = "Fetch")
@RestController
@RequestMapping(value = "/")
public class FetchController implements ApplicationContextAware {

    Logger logger = LoggerFactory.getLogger(getClass());

    private static Map<String, Object> fetcherMap = Collections.synchronizedMap(new HashMap<String, Object>());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> allClass = applicationContext.getBeansWithAnnotation(Fetcher.class);
        for (String key : allClass.keySet()) {
            Object object = allClass.get(key);
            Fetcher fetcher = object.getClass().getAnnotation(Fetcher.class);
            fetcherMap.put(fetcher.code(), object);
        }
    }

    @ApiOperation("全部Fetcher")
    @RequestMapping(value = "/fetchers", method = RequestMethod.GET)
    public Result getAllFetchers() {
        return new Result(fetcherMap);
    }
    @ApiOperation("个税页面初始化")
    @RequestMapping(value = "/asura/tax/init", method = RequestMethod.POST)
    public Result taxPageInit(TaxUser taxUser) {

        String cityCode= taxUser.getCityCode();
        logger.info("taxLogin:city" + cityCode);
        return ((IFetcher)fetcherMap.get(cityCode)).pageInit(taxUser);
    }


    @ApiOperation("个税登录")
    @RequestMapping(value = "/asura/tax/login", method = RequestMethod.POST)
    public Result taxLogin(TaxUser taxUser) {
        String cityCode= taxUser.getCityCode();
        logger.info("taxLogin:city" + cityCode);
        return ((IFetcher)fetcherMap.get(cityCode)).login(taxUser);
    }

    @ApiOperation("个税刷新验证码")
    @RequestMapping(value = "/asura/tax/refreshCaptcha", method = RequestMethod.POST)
    public Result taxRefreshCaptcha(TaxUser taxUser) {
        String cityCode= taxUser.getCityCode();
        logger.info("taxLogin:city" + cityCode);
        return ((IFetcher)fetcherMap.get(cityCode)).refreshCaptcha(taxUser);
    }

    @ApiOperation("个税刷新短信验证码")
    @RequestMapping(value = "/asura/tax/refreshSms", method = RequestMethod.POST)
    public Result taxRefreshSms(TaxUser taxUser) {
        String cityCode= taxUser.getCityCode();
        logger.info("taxLogin:city" + cityCode);
        return ((IFetcher)fetcherMap.get(cityCode)).refreshSms(taxUser);
    }
}
