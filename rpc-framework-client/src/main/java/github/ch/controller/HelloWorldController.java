package github.ch.controller;

import github.ch.annotation.RpcReference;
import github.ch.entity.SimpleEntity;
import github.ch.service.HelloWorld;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午7:10
 */
@Component
public class HelloWorldController {

    @RpcReference(version = "1.0", group = "1.0")
    private HelloWorld helloWorld;

    public void test() {
        System.out.println(helloWorld.hello(new SimpleEntity("你好", "世界")));
    }
}
