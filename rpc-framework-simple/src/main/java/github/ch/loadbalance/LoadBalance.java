package github.ch.loadbalance;

import github.ch.extension.SPI;

import java.util.List;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/27 下午12:50
 */
@SPI
public interface LoadBalance {
    String selectServiceAddress(String rpcServiceName, List<String> addressList);
}
