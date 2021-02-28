package github.ch.provider;

import github.ch.entity.RpcServiceProperties;
import github.ch.factory.SingletonFactory;
import github.ch.register.ServiceRegister;
import github.ch.register.zk.ZkServiceRegister;
import github.ch.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/27 下午1:30
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();
    private final ServiceRegister SERVICE_REGISTER;

    public ServiceProviderImpl() {
        SERVICE_REGISTER = SingletonFactory.getInstance(ZkServiceRegister.class);
    }

    @Override
    public void addService(Object service, RpcServiceProperties properties) {
        String serviceName = properties.toRpcServiceName();
        if (SERVICE_MAP.containsKey(serviceName))
            return;
        SERVICE_MAP.put(serviceName, service);
        log.info("add service [{}] and interfaces [{}]", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getService(RpcServiceProperties properties) {
        String serviceName = properties.toRpcServiceName();
        Object obj = null;
        try {
            if (SERVICE_MAP.containsKey(serviceName))
                obj = SERVICE_MAP.get(serviceName);
            else
                throw new Exception();
        } catch (Exception e) {
            log.error("must add service, then get service");
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public void publishService(Object service, RpcServiceProperties properties) {
        try {
            Class<?> anInterface = service.getClass().getInterfaces()[0];
            properties.setServiceName(anInterface.getCanonicalName());
            String serviceName = properties.toRpcServiceName();
            addService(service, properties);
            SERVICE_REGISTER.registerService(serviceName, new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.port));
        } catch (UnknownHostException e) {
            log.error("unknown host");
            e.printStackTrace();
        }
    }

    @Override
    public void publishService(Object service) {
        publishService(service, RpcServiceProperties.builder()
                .serviceName("")
                .version("")
                .group("")
                .build());
    }
}
