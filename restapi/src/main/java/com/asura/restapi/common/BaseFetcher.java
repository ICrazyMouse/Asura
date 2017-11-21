package com.asura.restapi.common;

import com.asura.restapi.model.TaxUser;
import com.asura.restapi.model.dto.TaskDto;
import com.asura.restapi.model.dto.TaxInfo;
import com.asura.restapi.service.TaskService;
import com.asura.restapi.service.TaxInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lichuanshun on 2017/11/20.
 */
public abstract class BaseFetcher extends AbstractHttpService<TaxUser>{

    //缓存
    protected MemcacheClient memcacheClient = MemcacheClient.getInstance();

    @Autowired
    protected TaskService taskService;
    @Autowired
    protected TaxInfoService taxInfoService;


    /**
     *  0: 错误 1：正常 2：正在登录 3:登录成功，正在解析 4：解析成功,开始保存
     */
    protected int TASK_STATUS_FAIL = 0;
    protected int TASK_STATUS_SUCCESS = 1;
    protected int TASK_STATUS_LOGGINING = 2;
    protected int TASK_STATUS_LOGIN_PARSING = 3;
    protected int TASK_STATUS_PARSE_SUCCESS = 4;

    /**
     * 任务进行中的缓存key
     *
     */
    //登录用cookie
    protected static String CACHE_KEY_FOR_LOGIN_COOKIE =  "login_cookie";
    // rsa公钥
    protected static String CACHE_KEY_FOR_RSA_PUBLICK_KEY = "rsa_publick_key";

    protected static List<String> CACAE_KEY_LIST = new ArrayList<>();
    static {
        CACAE_KEY_LIST.add(CACHE_KEY_FOR_LOGIN_COOKIE);
        CACAE_KEY_LIST.add(CACHE_KEY_FOR_RSA_PUBLICK_KEY);
    }



    /**
     * 解析完毕后清除此次任务的所有缓存
     * @param taskId
     */
    protected void clearMemcache(String taskId){
        for (String key: CACAE_KEY_LIST){
            String cacheKey = key + taskId ;
            memcacheClient.delete(cacheKey);
        }
    }


    /**
     * 缓存登录用cookie
     * @param taskId
     * @param cookie
     * @return
     */
    protected boolean cacheLoginCookie(String taskId,Object cookie){
        String cacheKey = CACHE_KEY_FOR_LOGIN_COOKIE + taskId;
        return memcacheClient.set(cacheKey,cookie);
    }

    /**
     * 缓存登录用cookie
     * @param taskId
     * @return
     */
    protected Object getCachedLoginCookie(String taskId){
        String cacheKey = CACHE_KEY_FOR_LOGIN_COOKIE + taskId;
        return memcacheClient.get(cacheKey);
    }
    /**
     * 缓存rsa 公钥
     * @param taskId
     * @param rsaPublicKey
     * @return
     */
    protected boolean cacheRsaPublicKey(String taskId,Object rsaPublicKey){
        String cacheKey = CACHE_KEY_FOR_RSA_PUBLICK_KEY + taskId;
        return memcacheClient.set(cacheKey,rsaPublicKey);
    }


    /**
     * 获取缓存rsa 公钥
     * @param taskId
     * @return
     */
    protected Object getCacheRsaPublicKey(String taskId){
        String cacheKey = CACHE_KEY_FOR_RSA_PUBLICK_KEY + taskId;
        return memcacheClient.get(cacheKey);
    }

    /**
     * 将任务状态设置为失败
     * @param taskId
     */
    protected void setTaskStatusFail(String taskId){
        updateTaskStatus(taskId, TASK_STATUS_FAIL);
    }


    /**
     * 将任务状态设置为成功
     * @param taskId
     */
    protected void setTaskStatusSuccess(String taskId){
        updateTaskStatus(taskId, TASK_STATUS_SUCCESS);
    }

    /**
     * 将任务状态设置为登录成功,正在解析
     * @param taskId
     */
    protected void setTaskStatusParsing(String taskId){
        updateTaskStatus(taskId, TASK_STATUS_LOGIN_PARSING);
    }
    /**
     * 将任务状态设置为登录成功,正在解析
     * @param taskId
     */
    protected void setTaskStatusParseSuccess(String taskId){
        updateTaskStatus(taskId, TASK_STATUS_PARSE_SUCCESS);
    }

    /**
     * 更新任务状态
     * @param taskId
     * @param status
     */
    private void updateTaskStatus(String taskId, int status){
        taskService.updateTaskStatus(status,taskId);
    }

    /**
     * 根据taskId获取任务信息
     * @param taskId
     * @return
     */
    protected TaskDto queryTaskByTaskId(String taskId){
        return taskService.queryTaskByTaskId(taskId);
    }


    /**
     * 保存税务信息
     * @param taxInfo
     * @return
     */
    protected int saveTaxInfo(TaxInfo taxInfo){
        return taxInfoService.saveTaxInfo(taxInfo);
    }

}
