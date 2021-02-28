package github.ch.remoting.transport.server;

import com.esotericsoftware.minlog.Log;
import github.ch.enums.CompressTypeEnum;
import github.ch.enums.RpcResponseCode;
import github.ch.enums.SerializationTypeEnum;
import github.ch.factory.SingletonFactory;
import github.ch.remoting.constants.RpcConstants;
import github.ch.remoting.dto.RpcMessage;
import github.ch.remoting.dto.RpcRequest;
import github.ch.remoting.dto.RpcResponse;
import github.ch.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/25 下午7:53
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage message = new RpcMessage();
                message.setCoderType(SerializationTypeEnum.PROTOSTUFF.getCode());
                message.setCompressType(CompressTypeEnum.GZIP.getCode());
                byte type = ((RpcMessage) msg).getMessageType();
                if (type == RpcConstants.HEART_REQUEST) {
                    log.info("server receives heart beat : [{}]", RpcConstants.PING);
                    message.setMessageType(RpcConstants.HEART_RESPONSE);
                    message.setData(RpcConstants.PONG);
                } else if (type == RpcConstants.REQUEST_TYPE) {
                    log.info("server receives message : [{}]", msg.toString());
                    message.setMessageType(RpcConstants.RESPONSE_TYPE);
                    RpcRequest request = (RpcRequest) (((RpcMessage) msg).getData());
                    Object result = rpcRequestHandler.handle(request);
                    log.info("get result : [{}]", result.toString());
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> success = RpcResponse.success(result, request.getRequestId());
                        message.setData(success);
                    } else {
                        RpcResponse<Object> fail = RpcResponse.fail(RpcResponseCode.FAIL);
                        message.setData(fail);
                        log.error("not writable now");
                    }
                    log.info("server sends message : [{}]", message.toString());
                }
                ctx.channel().writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen , so connection closed");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
