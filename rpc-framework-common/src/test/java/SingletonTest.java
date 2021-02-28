import github.ch.entity.RpcServiceProperties;
import github.ch.factory.SingletonFactory;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午10:37
 */
public class SingletonTest {
    public static void main(String[] args) {
        RpcServiceProperties properties = SingletonFactory.getInstance(RpcServiceProperties.class);
        System.out.println(properties.hashCode());
        RpcServiceProperties properties1 = SingletonFactory.getInstance(RpcServiceProperties.class);
        System.out.println(properties1.hashCode());
    }
}
