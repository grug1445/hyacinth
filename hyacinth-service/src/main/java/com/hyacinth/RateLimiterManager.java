package com.hyacinth;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by feichen on 2018/10/29.
 */
@Slf4j
public class RateLimiterManager {

    private String zkUrl = "localhost:2181";
    private Map<String, String> appNameRateValues;

    private ZkClient zkClient;


    void bootstrap() {
        this.zkClient = new ZkClient(this.zkUrl, 1000);
        appNameRateValues = new HashMap<>();
        appNameRateValues.put("hyacinth", "5");
        appNameRateValues.forEach((appServiceMethodName, value) -> {
            String path = HyacinthUtil.pathParse(appServiceMethodName, null);
            log.info("bootstrap appServiceMethodName ={}   value={} ", appServiceMethodName, value);
            //            zkClient.create(path, value, CreateMode.PERSISTENT);
            if (!zkClient.exists(path)) {
                zkClient.createPersistent(path);
            }
            zkClient.subscribeChildChanges(path, new ZkChildListener());
            zkClient.subscribeDataChanges(path, new ZkDataListener());
        });
    }


    /**
     * 子节点变更
     */
    private class ZkChildListener implements IZkChildListener {
        @Override
        public void handleChildChange(String parentPath, List<String> currentChildren) {
            try {
                log.info("ZkChildListener handleChildChange parentPath " + parentPath + " currentChildren" +
                        JSON.toJSONString(currentChildren));
            } catch (Exception e) {
            }
            if (CollectionUtils.isEmpty(currentChildren)) {
                return;
            }
            String rateValue = zkClient.readData(parentPath);
            if (rateValue == null) {
                return;
            }
            Double rateValuePerNode = Double.valueOf(rateValue) / currentChildren.size();
            currentChildren.forEach(child -> zkClient.writeData(parentPath + "/" + child, rateValuePerNode.toString()));
        }
    }

    /**
     * 数据变更
     */
    private class ZkDataListener implements IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object data) {
            List<String> children = zkClient.getChildren(dataPath);
            Double rateValuePerNode = Double.valueOf(data.toString()) / children.size();
            children.forEach(child -> zkClient.writeData(dataPath + "/" + child, rateValuePerNode));
            log.info("handleDataChange   " + dataPath + ":" + data.toString());
        }

        @Override
        public void handleDataDeleted(String dataPath) {
            log.info("handleDataDeleted " + dataPath);
        }
    }

}
