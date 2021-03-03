package github.ch.remoting.transport.client;

import github.ch.enums.CompressTypeEnum;
import github.ch.enums.SerializationTypeEnum;
import github.ch.extension.ExtensionLoader;
import github.ch.factory.SingletonFactory;
import github.ch.register.ServiceDiscovery;
import github.ch.register.zk.ZkServiceDiscovery;
import github.ch.remoting.constants.RpcConstants;
import github.ch.remoting.dto.RpcMessage;
import github.ch.remoting.dto.RpcRequest;
import github.ch.remoting.dto.RpcResponse;
import github.ch.remoting.transport.RpcRequestTransport;
import github.ch.remoting.transport.coder.RpcMessageDecoder;
import github.ch.remoting.transport.coder.RpcMessageEncoder;
import github.ch.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.jws.Oneway;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午1:20
 */
@Slf4j
public final class NettyRpcClient implements RpcRequestTransport {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequests unprocessedRequests;
    private final ServiceDiscovery serviceDiscovery;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new RpcMessageEncoder());
                        ch.pipeline().addLast(new RpcMessageDecoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successfully!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }
        });
        return completableFuture.get();
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();
        String rpcServiceName = rpcRequest.toRpcServiceProperties().toRpcServiceName();

        // 这里采用注册中心，暂时用new替代
        //InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.1.5", 8000);

        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);

        //获取对应的channel
        Channel channel = getChannel(inetSocketAddress);

        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), completableFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setData(rpcRequest);
            rpcMessage.setCoderType(SerializationTypeEnum.PROTOSTUFF.getCode());
            rpcMessage.setCompressType(CompressTypeEnum.GZIP.getCode());
            rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message : [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("send failed : ", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return completableFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public static void main(String[] args) {
        new NettyRpcClient().sendRequest(RpcRequest.builder()
                .requestId("1")
                .interfaceName("Hello")
                .methodName("add")
                .args(new Object[]{1000000000, 2})
                .argsType(new Class[]{int.class, int.class})
                .version("")
                .group("")
                .build());
    }
}
