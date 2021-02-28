package github.ch.remoting.transport.coder;


import github.ch.compress.gzip.GzipCompress;
import github.ch.enums.CompressTypeEnum;
import github.ch.enums.SerializationTypeEnum;
import github.ch.remoting.constants.RpcConstants;
import github.ch.remoting.dto.RpcMessage;
import github.ch.remoting.dto.RpcRequest;
import github.ch.remoting.dto.RpcResponse;
import github.ch.serialize.Serializer;
import github.ch.serialize.protostuff.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * custom protocol decoder
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 */

@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    private RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) decode;
            if (buf.readableBytes() >= RpcConstants.HEAD_LENGTH) {
                try {
                    return decodeFrame(buf);
                } catch (Exception e) {
                    log.error("decode error");
                    throw new Exception("decode error");
                } finally {
                    buf.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicCode(in);
        checkVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte coderType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .coderType(coderType)
                .compressType(compressType)
                .requestId(requestId)
                .build();
        if (messageType == RpcConstants.HEART_REQUEST) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEART_RESPONSE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }

        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] body = new byte[bodyLength];
            in.readBytes(body);
            GzipCompress compress = new GzipCompress();
            body = compress.decompress(body);
            Serializer serializer = new ProtostuffSerializer();
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest request = serializer.deserialize(body, RpcRequest.class);
                rpcMessage.setData(request);
            } else {
                RpcResponse response = serializer.deserialize(body, RpcResponse.class);
                rpcMessage.setData(response);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            log.error("version error");
            throw new IllegalArgumentException("version error : " + version);
        }
    }

    private void checkMagicCode(ByteBuf in) {
        byte[] bytes = new byte[RpcConstants.MAGIC_CODE.length];
        in.readBytes(bytes);
        for (int i = 0; i < bytes.length; i++)
            if (bytes[i] != RpcConstants.MAGIC_CODE[i]) {
                log.error("magic code error");
                throw new IllegalArgumentException("magic code error : " + Arrays.toString(bytes));
            }
    }
}
