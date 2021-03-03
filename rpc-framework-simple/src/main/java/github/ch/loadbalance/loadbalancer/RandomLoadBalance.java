package github.ch.loadbalance.loadbalancer;

import github.ch.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/3/3 上午9:47
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(String rpcServiceName, List<String> addressList) {
        return addressList.get(new Random().nextInt(addressList.size()));
    }
}
