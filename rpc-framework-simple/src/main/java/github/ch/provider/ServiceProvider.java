package github.ch.provider;

import github.ch.entity.RpcServiceProperties;

public interface ServiceProvider {
    void addService(Object service, RpcServiceProperties properties);

    Object getService(RpcServiceProperties properties);

    void publishService(Object service, RpcServiceProperties properties);

    void publishService(Object service);
}
