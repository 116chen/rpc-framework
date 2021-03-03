package github.ch.loadbalance;

import java.util.List;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/3/3 上午9:44
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(String rpcServiceName, List<String> addressList) {
        if (addressList == null || addressList.size() == 0)
            return null;
        if (addressList.size() == 1)
            return addressList.get(0);
        return doSelect(rpcServiceName, addressList);
    }

    protected abstract String doSelect(String rpcServiceName, List<String> addressList);
}
