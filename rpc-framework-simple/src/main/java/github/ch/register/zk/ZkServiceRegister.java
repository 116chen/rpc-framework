package github.ch.register.zk;

import github.ch.register.ServiceRegister;
import github.ch.register.zk.util.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/27 上午10:22
 */
public class ZkServiceRegister implements ServiceRegister {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        rpcServiceName = rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, rpcServiceName);
    }
}
