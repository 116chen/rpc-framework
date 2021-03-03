package github.ch.config;

import github.ch.register.zk.util.CuratorUtil;
import github.ch.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/3/3 上午11:16
 */
@Slf4j
public class CustomShutdownHook {
    private final static CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.port);
                CuratorUtil.clearRegister(CuratorUtil.getZkClient(), address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }));
    }
}

