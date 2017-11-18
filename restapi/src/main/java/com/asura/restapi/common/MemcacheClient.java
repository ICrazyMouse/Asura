package com.asura.restapi.common;

import com.alibaba.druid.util.StringUtils;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Date;
import java.util.Map;

/**
 */
public class MemcacheClient {
    private MemCachedClient mcc = new MemCachedClient();
    private static MemcacheClient instance = null;

    public MemcacheClient() {
    }

    public static synchronized MemcacheClient getInstance() {
        if(instance == null) {
            instance = new MemcacheClient();
            instance.init();
        }
        return instance;
    }

    public boolean add(String key, Object value) {
        boolean result = this.mcc.add(key, value);
        return result;
    }

    public boolean add(String key, Object value, int time) {
        boolean result = this.mcc.add(key, value, new Date(System.currentTimeMillis() + (long)time));
        return result;
    }

    public boolean set(String key, Object value) {
        boolean result = this.mcc.set(key, value);
        return result;
    }

    public boolean set(String key, Object value, int time) {
        boolean result = this.mcc.set(key, value, new Date(System.currentTimeMillis() + (long)time));
        return result;
    }

    public boolean append(String key, String value) {
        boolean result = this.mcc.append(key, value);
        return result;
    }

    public boolean append(String key, String value, String split) {
        boolean result = this.mcc.append(key, split + value);
        return result;
    }

    public boolean append(String key, String value, int time) {
        boolean flag = false;
        String result = (String)this.mcc.get(key);
        if(!StringUtils.isEmpty(result)) {
            flag = this.mcc.set(key, value + value, new Date(System.currentTimeMillis() + (long)time));
        }

        return flag;
    }

    public boolean append(String key, String value, String split, int time) {
        boolean flag = false;
        String result = (String)this.mcc.get(key);
        if(!StringUtils.isEmpty(result)) {
            flag = this.mcc.set(key, result + split + value, new Date(System.currentTimeMillis() + (long)time));
        }

        return flag;
    }

    public boolean delete(String key) {
        return this.mcc.delete(key);
    }

    public boolean keyExists(String key) {
        return this.mcc.keyExists(key);
    }

    public Object get(String key) {
        return this.mcc.get(key);
    }

    public Map<String, Object> getMulti(String[] key) {
        return this.mcc.getMulti(key);
    }

    public void init() {
        Config pushConf = ConfigFactory.load("memcache.properties");
        SockIOPool pool = SockIOPool.getInstance();
        String memcache_ip = pushConf.getString("memcache.servers");
        String [] addr = memcache_ip.split("\\,");

        String[] weightarr = pushConf.getString("memcache.weights").split(",");
        Integer[] weights = new Integer[weightarr.length];
        for(int i = 0; i < weightarr.length; ++i) {
            weights[i] = new Integer(weightarr[i]);
        }

        pool.setServers(addr);
        pool.setWeights(weights);
        pool.setFailover(pushConf.getBoolean("memcache.failover"));
        pool.setInitConn(pushConf.getInt("memcache.initConn"));
        pool.setMinConn(pushConf.getInt("memcache.minConn"));
        pool.setMaxConn(pushConf.getInt("memcache.maxConn"));
        pool.setMaxIdle(1000 * 30 * 30);
        pool.setMaintSleep(pushConf.getLong("memcache.maintSleep"));
        pool.setNagle(pushConf.getBoolean("memcache.nagle"));
        pool.setSocketTO(pushConf.getInt("memcache.socketTO"));
        pool.setAliveCheck(pushConf.getBoolean("memcache.aliveCheck"));
        pool.initialize();
    }

    public static void main(String[] args) {
        MemcacheClient memCachedClient=MemcacheClient.getInstance();
        memCachedClient.add("test","1");
        System.out.println(memCachedClient.get("test"));

    }
}
