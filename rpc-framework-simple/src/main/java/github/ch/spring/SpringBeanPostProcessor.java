package github.ch.spring;

import github.ch.annotation.RpcReference;
import github.ch.annotation.RpcService;
import github.ch.entity.RpcServiceProperties;
import github.ch.factory.SingletonFactory;
import github.ch.provider.ServiceProvider;
import github.ch.provider.ServiceProviderImpl;
import github.ch.proxy.RpcClientProxy;
import github.ch.remoting.transport.RpcRequestTransport;
import github.ch.remoting.transport.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午2:42
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.rpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                    .version(annotation.version())
                    .group(annotation.group())
                    .build();
            serviceProvider.publishService(bean, serviceProperties);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            RpcReference fieldAnnotation = field.getAnnotation(RpcReference.class);
            if (fieldAnnotation == null)
                continue;
            RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                    .version(fieldAnnotation.version())
                    .group(fieldAnnotation.group())
                    .build();
            RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, serviceProperties);
            Object proxy = rpcClientProxy.getProxy(field.getType());
            field.setAccessible(true);
            try {
                field.set(bean, proxy);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
}
