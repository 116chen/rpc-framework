package github.ch.register;

import github.ch.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(String rpcServiceName);
}
