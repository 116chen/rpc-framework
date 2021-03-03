package github.ch;

import github.ch.annotation.RpcScan;
import github.ch.remoting.transport.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午5:44
 */
@RpcScan(basePackage = "github.ch")
public class NettyServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer server = new NettyRpcServer();
        server.start();
    }
}
