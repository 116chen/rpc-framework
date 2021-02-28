package github.ch;

import github.ch.annotation.RpcScan;
import github.ch.controller.HelloWorldController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午7:10
 */
@RpcScan(basePackage = "github.ch.controller")
public class NettyClientMain {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloWorldController helloWorldController = (HelloWorldController) context.getBean("helloWorldController");
        helloWorldController.test();
    }
}