package com.asura.restapi.common;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.util.Map;

/**
 * Created by been on 2017/4/18.
 */
public class LoginContext {

    private final BasicCookieStore cookieStore;
    private final CloseableHttpClient httpClient;
    private final Map<String, String> requestHeaders;
    private final HttpContext httpContext;
    private final RequestConfig requestConfig;


    private String uri;
    private String encoding = "UTF-8";
    //请求参数
    private Map<String, String> params;

    //响应信息
    private Header[] responseHeaders;
    private StatusLine responseStatusLine;

    private HttpEntity responseEntity;

    public LoginContext(BasicCookieStore cookieStore,
                        CloseableHttpClient httpClient,
                        Map<String, String> headers,
                        HttpContext httpContext,
                        RequestConfig requestConfig) {
        this.cookieStore = cookieStore;
        this.httpClient = httpClient;
        this.requestHeaders = headers;
        this.httpContext = httpContext;
        this.requestConfig = requestConfig;
    }

    /**
     * 每次发起请求之前清空
     */
    public void clear() {
        this.responseHeaders = null;
        this.responseStatusLine = null;
        this.responseEntity = null;
    }

    public void setResponseInfo(CloseableHttpResponse response) {
        this.responseHeaders = response.getAllHeaders();
        this.responseStatusLine = response.getStatusLine();
        this.responseEntity = response.getEntity();
    }


    public HttpEntity getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(HttpEntity responseEntity) {
        this.responseEntity = responseEntity;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public StatusLine getResponseStatusLine() {
        return responseStatusLine;
    }

    public void setResponseStatusLine(StatusLine responseStatusLine) {
        this.responseStatusLine = responseStatusLine;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public String getCookieStr() {
        StringBuffer sb = new StringBuffer();
        for (Cookie cookie : cookieStore.getCookies()) {
            String name = cookie.getName();
            String value = cookie.getValue();
            sb.append(name + "=" + value).append(";");
        }
        return sb.toString();
    }
}
