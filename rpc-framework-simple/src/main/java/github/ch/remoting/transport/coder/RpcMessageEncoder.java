package github.ch.remoting.transport.coder;


import github.ch.compress.Compress;
import github.ch.compress.gzip.GzipCompress;
import github.ch.enums.CompressTypeEnum;
import github.ch.enums.SerializationTypeEnum;
import github.ch.remoting.constants.RpcConstants;
import github.ch.remoting.dto.RpcMessage;
import github.ch.serialize.Serializer;
import github.ch.serialize.protostuff.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * <p>
 * custom protocol decoder
 * <p>
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
 * </pre>
 */

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private final static AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_CODE);
            byteBuf.writeByte(RpcConstants.VERSION);
            byteBuf.writerIndex(byteBuf.writerIndex() + Integer.BYTES);
            byteBuf.writeByte(rpcMessage.getMessageType());
            byteBuf.writeByte(rpcMessage.getCoderType());
            byteBuf.writeByte(rpcMessage.getCompressType());
            byteBuf.writeInt(ATOMIC_INTEGER.incrementAndGet());

            byte[] body = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            if (rpcMessage.getMessageType() != RpcConstants.HEART_REQUEST
                    && rpcMessage.getMessageType() != RpcConstants.HEART_RESPONSE) {
                Serializer serializer = new ProtostuffSerializer();
                body = serializer.serialize(rpcMessage.getData());
                Compress compress = new GzipCompress();
                body = compress.compress(body);
                fullLength += body.length;
            }

            if (body != null) {
                byteBuf.writeBytes(body);
            }
            int i = byteBuf.writerIndex();
            byteBuf.writerIndex(i - fullLength + RpcConstants.MAGIC_CODE.length + RpcConstants.VERSION_LENGTH);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(i);
        } catch (Exception e) {
            log.error("encode request failed");
        }
    }
}

