package github.ch.test;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/21 下午9:49
 */
public class NettyServerTest {
    public static void main(String[] args) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 3306);
        System.out.println(inetSocketAddress.toString());
    }
}
