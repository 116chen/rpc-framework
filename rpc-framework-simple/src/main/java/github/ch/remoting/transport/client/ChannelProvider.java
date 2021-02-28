package github.ch.remoting.transport.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午10:47
 */
@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (CHANNEL_MAP.containsKey(key)) {
            Channel channel = CHANNEL_MAP.getOrDefault(key, null);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                CHANNEL_MAP.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        CHANNEL_MAP.put(key, channel);
    }
}
