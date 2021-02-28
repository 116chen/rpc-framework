package github.ch.remoting.transport.server;

import com.sun.media.jfxmedia.logging.Logger;
import github.ch.factory.SingletonFactory;
import github.ch.provider.ServiceProvider;
import github.ch.provider.ServiceProviderImpl;
import github.ch.register.ServiceRegister;
import github.ch.register.zk.ZkServiceRegister;
import github.ch.remoting.dto.RpcRequest;
import github.ch.remoting.handler.RpcRequestHandler;
import github.ch.remoting.transport.coder.RpcMessageDecoder;
import github.ch.remoting.transport.coder.RpcMessageEncoder;
import github.ch.test.HelloImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/25 下午3:06
 */
@Slf4j
@Component
public class NettyRpcServer {
    public static final int port = 8000;

    private ServiceRegister serviceRegister = SingletonFactory.getInstance(ZkServiceRegister.class);

    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        serviceRegister.registerService(rpcServiceName, inetSocketAddress);
    }

    @SneakyThrows
    public void start() {
        //自动关闭所有服务

        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new RpcMessageEncoder());
                            socketChannel.pipeline().addLast(new RpcMessageDecoder());
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server : ", e);
        } finally {
            log.error("shutdown eventLoopGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        //RpcRequestHandler rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
        NettyRpcServer server = new NettyRpcServer();
        RpcRequest request = RpcRequest.builder()
                .interfaceName("Hello")
                .version("")
                .group("")
                .build();

        String s = request
                .toRpcServiceProperties()
                .toRpcServiceName();

        ServiceProvider provider = SingletonFactory.getInstance(ServiceProviderImpl.class);

        //这里使用HelloImpl暂时替代，后续使用注解完成
        provider.addService(new HelloImpl(), request.toRpcServiceProperties());

        server.registerService(s, new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port));
        server.start();
    }

}
