package com.asura.restapi.fetcher;

import com.asura.restapi.annotations.Fetcher;
import com.asura.restapi.api.IFetcher;

/**
 * Created by Mario on 2017/11/13 0013.
 * 测试
 */
@Fetcher(code = "007")
public class TestFetcher implements IFetcher{
    private String name = "我是测试的";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
