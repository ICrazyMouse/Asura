package com.asura.restapi.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lichuanshun on 2017/11/3.
 */
public abstract class AbstractHttpService<M> implements BaseService {

    public int mecheTime = 10;
    public int loopCount = 5;

    public static final Map<String, LoginContext> CONTEXT_MAP = new ConcurrentHashMap<>();

    private static PoolingHttpClientConnectionManager connectionManager = null;

    public Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractHttpService(){
        synchronized (AbstractHttpService.class){
            if (connectionManager == null){
                connectionManager = initConnManager();
            }
        }
    }

    public void removeLoginContext(JSONObject params) {
        String userId = params.getString("userId");
        CONTEXT_MAP.remove(userId);
    }
    /**
     * 退出登录
     *
     * @param params
     * @throws Exception
     */
    public abstract void logout(JSONObject params) throws Exception;
    /**
     * 读取console
     * @return
     */
    public String readConsole(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }


    /**
     * 登录并解析数据,返回解析结果
     *
     * @param params
     * @return
     * @throws Exception
     */
    public abstract M loginAndParseInfo(JSONObject params) throws Exception;

    private PoolingHttpClientConnectionManager initConnManager() {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //信任任何链接
            TrustStrategy anyTrustStrategy = (x509Certificates, s) -> true;
            SSLContext sslContext = SSLContexts
                    .custom()
//                    .useTLS()
                    .useProtocol("TLS")
                    .loadTrustMaterial(trustStore, anyTrustStrategy)
                    .build();
            NoopHostnameVerifier noopHostnameVerifier = NoopHostnameVerifier.INSTANCE;
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
//                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
                    noopHostnameVerifier
            );
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(2000);
        connectionManager.setDefaultMaxPerRoute(50);
        return connectionManager;
    }

    protected LoginContext createLoginContext(BasicCookieStore cookieStore){
        return createLoginContext(cookieStore,null);
    }

    /**
     * 创建loginContext
     * @param cookieStore 缓存对象
     * @param proxy 代理
     * @return loginContext 上下文对象
     */
    protected LoginContext createLoginContext(BasicCookieStore cookieStore,HttpHost proxy) {
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .setProxy(proxy)
                .build();
        CloseableHttpClient httpClient = getHttpClient(cookieStore);
        return new LoginContext(cookieStore, httpClient, getBasicHeader(), localContext, requestConfig);
    }

    protected Map<String, String> getBasicHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Connection", "keep-alive");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/47.0.2526.106 Safari/537.36");
        return headers;
    }

    /**
     * httpClient get
     *
     * @param context 请求上下文
     * @return 请求结果：
     * 没有请求地址uri，返回 null
     * 状态码为 200， 返回请求结果
     * 返回结果不为200， 返回状态码
     */
    public String doGet(LoginContext context) {
        context.clear();
        String uri = context.getUri();
        if (StringUtil.isBlank(uri)) return null;
        CloseableHttpClient httpClient = context.getHttpClient();
        CloseableHttpResponse response = null;
        String result = "";
        try {
            String encode = context.getEncoding();
            Map<String, String> params = context.getParams();
            if (params != null && !params.isEmpty()) {
                // 设置请求参数
                List<NameValuePair> getParams = new ArrayList<>();
                params.forEach((k, v) -> getParams.add(new BasicNameValuePair(k, v)));
                String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(getParams, encode));
                uri += "?" + paramsStr;
            }
            HttpGet httpGet = new HttpGet(uri);

            Map<String, String> headers = context.getRequestHeaders();
            // 设置请求头
            if (headers != null) setCustomHeader(httpGet, headers);

            RequestConfig requestConfig = context.getRequestConfig();
            // 其他配置
            if (requestConfig != null) httpGet.setConfig(requestConfig);

            response = httpClient.execute(httpGet, context.getHttpContext());

            context.setResponseInfo(response);
            // 获取信息
            HttpEntity entity = response.getEntity();
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if ("200".equals(statusCode)) {
                result = readHttpContent(entity, encode);
            } else {
                result = statusCode;
            }
        } catch (Exception e) {
            logger.error("httpClient get " + uri + " 异常：", e);
        } finally {
            closeAndReturnHttpConnection(response);
        }
        return result;
    }

    public String getCookieParam(String cookies,String param) {
        int begin = cookies.lastIndexOf(param);
        String beginStr = cookies.substring(begin);
        int end = beginStr.indexOf(";");
        return beginStr.substring(0, end);
    }
    protected void setCustomHeader(HttpRequestBase httpRequest, Map<String, String> headers) {
        headers.forEach((k, v) -> httpRequest.setHeader(k, v));
    }

    public CloseableHttpClient getHttpClient(BasicCookieStore cookieStore) {
        return getHttpClientFromConnManager(cookieStore, null);
    }

    /**
     * @param cookieStore
     * @return
     */
    public CloseableHttpClient getHttpClientFromConnManager(BasicCookieStore cookieStore, HttpHost proxy) {
        return HttpClientBuilder
                .create()
                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(connectionManager).build();
    }
    /**
     * httpClient post
     *
     * @param context 请求上下文
     * @return 请求结果：
     * 没有请求地址uri，返回 null
     * 状态码为 200， 返回请求结果
     * 状态码为 302， 返回跳转地址
     * 其他情况， 返回状态码
     */
    public String doPost(LoginContext context) {
        context.clear();
        String uri = context.getUri();
        if (StringUtil.isBlank(uri)) return null;
        CloseableHttpClient httpClient = context.getHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpResponse response = null;
        String result = "";
        try {
            Map<String, String> headers = context.getRequestHeaders();
            // 设置请求头
            if (headers != null) setCustomHeader(httpPost, headers);

            String encode = context.getEncoding();
            Map<String, String> params = context.getParams();
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> postParam = new ArrayList<>();
                params.forEach((k, v) -> postParam.add(new BasicNameValuePair(k, v)));
                UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(postParam, encode);
                httpPost.setEntity(postEntity);
            }

            RequestConfig requestConfig = context.getRequestConfig();
            // 其他配置
            if (requestConfig != null) httpPost.setConfig(requestConfig);

            response = httpClient.execute(httpPost, context.getHttpContext());
            context.setResponseInfo(response);


            // 获取信息
            HttpEntity entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode:" + statusCode);
            if (statusCode == 200) {
                result = readHttpContent(entity, encode);
            } else {
                result = String.valueOf(response.getStatusLine().getStatusCode());
                if ("302".equals(result)||"301".equals(result)) {
                    result += response.getFirstHeader("Location").getValue();
                }
            }
        } catch (Exception e) {
            logger.error("post " + uri + "异常", e);
        } finally {
            closeAndReturnHttpConnection(response);
        }
        return result;
    }

    /**
     * 确定验证码的存放位置
     *
     * @return
     */
    public String getYzmLocation() {
        String location = "yzm";
        if (OSUtil.isLinux()) {
            location = "/opt/export/data/gjj/image/yzm/";
        } else if (OSUtil.isWindows()) {
            location = "c://yzm/";
        }

        logger.info("验证码所在路径: " + location);
        return location;
    }

    /**
     * 保存图片
     *
     * @param cuserid
     * @param response
     * @return storm中返回文件名称, 本地测试返回文件保存路径
     * @throws Exception
     */
    protected String saveImageFile(String cuserid, CloseableHttpResponse response) throws Exception {
        String filename = cuserid + ".png";
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getYzmLocation() + filename));
            entity.writeTo(bos);
            logger.info("将验证码保存到: " + getYzmLocation() + filename);
            bos.close();
        }
        if (OSUtil.isLinux()) {
            //集群中
            return filename;
        } else {
            //mac or windows
            return getYzmLocation() + filename;
        }
    }


    /**
     * 本地测试生成图片文件
     * 将图片信息采用base64加密
     *
     * @param cuserid
     * @param response
     * @return 返回base64加密的字符串
     * @throws Exception
     */
    protected String saveYZM(String cuserid, CloseableHttpResponse response) throws Exception {
        // TODO: 2017/11/18 暂时注释
//        if (!OSUtil.isLinux()) {
//            return saveImageFile(cuserid, response);
//        }

//        saveImageFile(cuserid, response);

        HttpEntity entity = response.getEntity();
        byte[] bytes = EntityUtils.toByteArray(entity);
        closeAndReturnHttpConnection(response);
//        return new BASE64Encoder().encode(bytes);
        return Base64.encodeBase64String(bytes);
    }
    /**
     * 将yzm 保存到磁盘
     *
     * @param resourcesDataPath
     * @param imageName
     * @param localBufferedImage
     * @return
     * @throws Exception
     */
    public String saveYzm(String resourcesDataPath, String imageName,
                          BufferedImage localBufferedImage) throws Exception {
        File dir = new File(resourcesDataPath);
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            if (!created) {
                throw new RuntimeException("无法创建目录: " + dir.getAbsolutePath());
            }
        }
        logger.info("");
        File file = new File(resourcesDataPath, imageName);
        //将验证码图片存入文件系统
        boolean result = ImageIO.write(localBufferedImage, "gif", file);
        if (result) {
            return file.getName();
        } else {
            return null;
        }
    }
    /**
     * 使用js引擎执行脚本加密
     * @param path 脚本文件位置
     * @param func 执行加密的方法
     * @param params 调用加密方法的参数
     * @return 加密结果
     */
    public String encryStr(String path,String func,Object... params){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        InputStream inputStream = getClass().getResourceAsStream(path);
        try {
            engine.eval(new InputStreamReader(inputStream));
            if(engine instanceof Invocable) {
                Invocable invoke = (Invocable)engine;
                return (String) invoke.invokeFunction(func, params);
            }
        } catch (Exception e) {
            logger.error(getClass().getSimpleName() + " ---- 异常", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    protected String doYzm(String cuserid,String url,
                           LoginContext context){
        context.clear();
        CloseableHttpClient httpClient = context.getHttpClient();
        CloseableHttpResponse response = null;
        String result = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            Map<String, String> headers = context.getRequestHeaders();
            // 设置请求头
            if (headers != null) setCustomHeader(httpGet, headers);

            RequestConfig requestConfig = context.getRequestConfig();
            // 其他配置
            if (requestConfig != null) httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet, context.getHttpContext());
            context.setResponseInfo(response);
            // 获取信息
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if ("200".equals(statusCode)) {
                return saveYZM(cuserid, response);
            } else {
                logger.info(cuserid+" url>>"+url+";statusCode>>"+statusCode);
                result = statusCode;
            }
        } catch (Exception e) {
            logger.error(cuserid+" httpClient get " + url + " 异常：", e);
        } finally {
            try {
                closeAndReturnHttpConnection(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * post请求不是form表单类型 @StringEntity
     *
     * @param loginContext
     * @param payload
     * @return
     */
    protected String httpPostPayload(LoginContext loginContext, String payload) {
        String context = "";
        CloseableHttpResponse response = null;
        try {
            loginContext.clear();
            String url = loginContext.getUri();

            String encode = loginContext.getEncoding();
            CloseableHttpClient httpClient = loginContext.getHttpClient();
            BasicCookieStore cookieStore = loginContext.getCookieStore();

            HttpPost post = new HttpPost(url);
            //提交的json数据
            StringEntity entity = new StringEntity(payload);
            entity.setContentEncoding(encode);
            entity.setContentType("application/json");
            post.setEntity(entity);

            Map<String, String> map = loginContext.getRequestHeaders();
            setCustomHeader(post, map);

            response = httpClient.execute(post, loginContext.getHttpContext());
            loginContext.setResponseInfo(response);
            String statusCode = response.getStatusLine().toString();

            if ("HTTP/1.1 200 OK".equals(statusCode)) {
                context = readHttpContent(response.getEntity(), encode);
            } else {
                context = String.valueOf(response.getStatusLine().getStatusCode());
                if ("302".equals(context)||"301".equals(context)) {
                    context += response.getFirstHeader("Location").getValue();
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            closeAndReturnHttpConnection(response);
        }
        return context;
    }

}
