package github.ch.test;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/26 下午8:52
 */
public class ZookeeperTest {
    private static final int BASE_SLEEP_TIME = 100;
    private static final int MAX_RETRIES = 3;
    private static ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
    private static CuratorFramework zkClient = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .retryPolicy(retry)
            .build();

    public static void main(String[] args) throws Exception {
        zkClient.start();

//        zkClient.create()
//                .creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .forPath("/node1/00001");

//        zkClient.create()
//                .creatingParentsIfNeeded()
//                .withMode(CreateMode.EPHEMERAL)
//                .forPath("/node1/00002");

//        Stat stat = zkClient.checkExists().forPath("/node1/00001");
//        System.out.println(stat != null);

//        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");

//        zkClient.create().creatingParentsIfNeeded().forPath("/node1/node1.1", "java".getBytes());
//        byte[] bytes = zkClient.getData().forPath("/node1/node1.1");
//        System.out.println(new String(bytes));
//        zkClient.setData().forPath("/node1/node1.1", "jvav".getBytes());
//        bytes = zkClient.getData().forPath("/node1/node1.1");
//        System.out.println(new String(bytes));

        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, "/node1", true);
        pathChildrenCache
                .getListenable()
                .addListener((curatorFramework, pathChildrenCacheEvent) -> {
                    System.out.println(pathChildrenCacheEvent.getType());
                });
        pathChildrenCache.start();

        zkClient.create().creatingParentsIfNeeded().forPath("/node1/node1.1");

        Thread.sleep(1000 * 10);//这里需要添加休眠时间，否则会因为结束太快而抛出异常
        pathChildrenCache.close();
        zkClient.close();
    }
}
