package github.ch.service;


import github.ch.annotation.RpcService;
import github.ch.entity.SimpleEntity;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午5:40
 */
@RpcService(version = "1.0", group = "1.0")
public class HelloWorldImpl implements HelloWorld {
    @Override
    public String hello(SimpleEntity simpleEntity) {
        return simpleEntity.getSendMessage() + "$这是一个RPC框架$" + simpleEntity.getReceiveMessage();
    }
}
