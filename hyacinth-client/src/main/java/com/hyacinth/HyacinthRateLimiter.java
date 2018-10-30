package com.hyacinth;

import com.google.common.util.concurrent.RateLimiter;
import com.hyacinth.exception.HyacinthException;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

/**
 * Created by feichen on 2018/10/29.
 */
public class HyacinthRateLimiter {
    private String zkUrl;
    private String appServiceMethodName;
    private Double defaultRateValue;
    private String localHost;
    private RateLimiter rateLimiter;

    /**
     * @param zkUrl                zk地址
     * @param appServiceMethodName 接口名#方法名
     * @param defaultRateValue     默认单台机器qps
     * @param rateLimiter          rateLimiter实例
     */
    public HyacinthRateLimiter(String zkUrl,
                               String appServiceMethodName,
                               Double defaultRateValue,
                               String localHost,
                               RateLimiter rateLimiter) {
        this.zkUrl = zkUrl;
        this.defaultRateValue = defaultRateValue;
        this.appServiceMethodName = appServiceMethodName;
        this.rateLimiter = rateLimiter;
        this.localHost = localHost;
        initZk();
    }

    /**
     * @param zkUrl                zk地址
     * @param appServiceMethodName 接口名#方法名
     * @param defaultRateValue     默认单台机器qps
     */
    public HyacinthRateLimiter(String zkUrl, String appServiceMethodName, Double defaultRateValue, String localHost) {
        this.zkUrl = zkUrl;
        this.defaultRateValue = defaultRateValue;
        this.appServiceMethodName = appServiceMethodName;
        this.rateLimiter = RateLimiter.create(defaultRateValue);
        this.localHost = localHost;
        initZk();
    }

    public double acquire() {
        return this.rateLimiter.acquire();
    }

    public double acquire(int permits) {
        return this.rateLimiter.acquire(permits);
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return this.rateLimiter.tryAcquire(1, timeout, unit);
    }

    public boolean tryAcquire(int permits) {
        return this.rateLimiter.tryAcquire(permits, 0, MICROSECONDS);
    }

    public boolean tryAcquire() {
        return this.rateLimiter.tryAcquire(1, 0, MICROSECONDS);
    }

    /**
     * zk初始化
     */
    private void initZk() {
        try {
            ZkClient zkClient = new ZkClient(this.zkUrl, 1000);
            String path = HyacinthUtil.pathParse(this.appServiceMethodName, this.localHost);
            if (!zkClient.exists(path)) {
                zkClient.create(path, this.defaultRateValue, CreateMode.EPHEMERAL);
            }
            zkClient.subscribeDataChanges(path, new ZkDataListener());
            System.out.println("initZk path  " + path + " defaultValue  " + defaultRateValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HyacinthException("HyacinthRateLimiter 初始化异常");
        }
    }

    /**
     * 数据变更
     */
    private class ZkDataListener implements IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object data) {
            rateLimiter.setRate((Double.valueOf(data.toString())));
            System.out.println("handleDataChange   " + dataPath + ":" + data.toString());
        }

        @Override
        public void handleDataDeleted(String dataPath) {
            System.out.println("handleDataDeleted " + dataPath);
        }
    }

}
