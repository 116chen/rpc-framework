package github.ch.loadbalance;

import java.util.List;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/27 下午12:50
 */
public class LoadBalance {
    public String selectServiceAddress(String rpcServiceName, List<String> addressList) {
        return addressList.get(0);
    }
}
