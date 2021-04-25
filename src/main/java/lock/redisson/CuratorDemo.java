package lock.redisson;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author mood321
 * @date 2021/4/25 23:49
 * @email 371428187@qq.com
 *
 */
public class CuratorDemo {
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.128.129:2181",
                5000, 5000, retryPolicy);

        
        PathAndBytesable<String> createBuilder = client.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL);

    }
}