package com.hyacinth.zk;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by feichen on 2018/10/29.
 */
public class ClientTest {

    static class User implements Serializable {
        private Long id;
        private String name;

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public User() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" + "id=" + id + ", name='" + name + '\'' + '}';
        }
    }

    public static void main(String[] args) {
        ZkClient zkClient = create("localhost:2181");
//        User user = new User(1L, "test1");
//        String path = "/testPath/hahadong";
//        nodeDelete(zkClient, path);
//        createNode(zkClient, path, user);
//        getNode(zkClient, path);
//        getChild(zkClient, path);
//        nodeExists(zkClient, path);
//        User user2 = new User(2L, "test2");
        zkClient.writeData("/hyacinth",  Double.valueOf(1.0), 8);
//        nodeDelete(zkClient, path);
    }

    /**
     * 创建连接
     *
     * @param url
     * @return
     */
    private static ZkClient create(String url) {
        ZkClient zkClient = new ZkClient(url, 1000);
        System.out.println("connected ok");
        return zkClient;
    }

    /**
     * 创建节点
     *
     * @param zkClient
     * @param path
     */
    private static void createNode(ZkClient zkClient, String path, User user) {
        String resultPath = zkClient.create(path, user, CreateMode.EPHEMERAL);
        zkClient.subscribeChildChanges(path, new ZkChildListener());
        zkClient.subscribeDataChanges(path, new ZkDataListener());
//        zkClient.create(path + "/testChild", "child", CreateMode.EPHEMERAL);
        System.out.println("create path " + resultPath);
    }

    /**
     * 取节点
     *
     * @param zkClient
     * @param path
     */
    private static void getNode(ZkClient zkClient, String path) {
        Stat stat = new Stat();
        User user = zkClient.readData(path, stat);
        System.out.println("getNode " + user.toString() + " stat " + JSON.toJSONString(stat));
    }

    /**
     * 取子节点
     *
     * @param zkClient
     * @param path
     */
    private static void getChild(ZkClient zkClient, String path) {
        List<String> nodes = zkClient.getChildren(path);
        System.out.println("getChild " + nodes.toString());
    }

    /**
     * 检测节点
     *
     * @param zkClient
     * @param path
     */
    private static void nodeExists(ZkClient zkClient, String path) {
        System.out.println("nodeExists " + path + " result is " + zkClient.exists(path));
    }

    /**
     * 节点删除
     *
     * @param zkClient
     * @param path
     */
    private static void nodeDelete(ZkClient zkClient, String path) {
        System.out.println("zk.deleteRecursive " + zkClient.deleteRecursive(path));
    }

    /**
     * 修改数据
     *
     * @param zkClient
     * @param path
     * @param user
     */
    private static void modifyNode(ZkClient zkClient, String path, User user, int version) {
        zkClient.writeData(path, user, version);
        System.out.println("modifyNode done");
    }

    /**
     * 子节点变更
     */
    private static class ZkChildListener implements IZkChildListener {
        public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
            System.out.println("ZkChildListener handleChildChange parentPath " + parentPath + " currentChildren" +
                    currentChildren.toString());
        }
    }

    /**
     * 数据变更
     */
    private static class ZkDataListener implements IZkDataListener {
        public void handleDataChange(String dataPath, Object data) throws Exception {
            System.out.println("handleDataChange   " + dataPath + ":" + data.toString());
        }

        public void handleDataDeleted(String dataPath) throws Exception {
            System.out.println("handleDataDeleted " + dataPath);
        }
    }


}
