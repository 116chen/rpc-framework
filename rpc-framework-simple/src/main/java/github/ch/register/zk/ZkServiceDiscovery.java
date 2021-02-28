package github.ch.register.zk;

import github.ch.factory.SingletonFactory;
import github.ch.loadbalance.LoadBalance;
import github.ch.register.ServiceDiscovery;
import github.ch.register.zk.util.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/27 上午10:22
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        loadBalance = SingletonFactory.getInstance(LoadBalance.class);
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        InetSocketAddress inetSocketAddress = null;
        try {
            List<String> addressList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);
            if (addressList == null || addressList.size() == 0) {
                throw new Exception();
            }
            String address = loadBalance.selectServiceAddress(rpcServiceName, addressList);
            String[] tmpValue = address.split(":");
            inetSocketAddress = new InetSocketAddress(tmpValue[0], Integer.parseInt(tmpValue[1]));
            log.info("look up service successfully");
        } catch (Exception e) {
            log.error("look up service failed");
            e.printStackTrace();
        }
        return inetSocketAddress;
    }
}
