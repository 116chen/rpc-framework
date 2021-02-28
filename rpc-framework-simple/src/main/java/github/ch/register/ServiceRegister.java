package github.ch.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
