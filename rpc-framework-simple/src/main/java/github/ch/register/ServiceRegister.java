package github.ch.register;

import github.ch.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceRegister {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
