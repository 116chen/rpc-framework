package github.ch.loadbalance.loadbalancer;

import github.ch.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/3/3 上午9:48
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final static ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected String doSelect(String rpcServiceName, List<String> addressList) {
        int hashCode = addressList.hashCode();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        if (selector == null || selector.identityHashCode != hashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(addressList, hashCode, 160));
            selector = selectors.get(rpcServiceName);
        }
        return selector.doSelect(rpcServiceName);
    }

    private static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;
        private final long identityHashCode;

        public ConsistentHashSelector(List<String> addressList, long identityHashCode, int replicaNumber) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            for (String address : addressList) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] bytes = md5(address + i);
                    for (int h = 0; h < 4; h++) {
                        long hashCode = hash(bytes, 0);
                        virtualInvokers.put(hashCode, address);
                    }
                }
            }
        }

        private static byte[] md5(String key) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(key.getBytes(StandardCharsets.UTF_8));
                return md5.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static long hash(byte[] bytes, int idx) {
            return (((long) (bytes[3 + idx * 4] & 0xFF) << 24)
                    | ((long) (bytes[2 + idx * 4] & 0xFF) << 16)
                    | ((long) (bytes[1 + idx * 4] & 0xFF) << 8)
                    | (bytes[idx * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public String doSelect(String rpcServiceName) {
            byte[] key = md5(rpcServiceName);
            return doSelectForKey(hash(key, 0));
        }

        private String doSelectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.ceilingEntry(hashCode);
            if (entry == null)
                entry = virtualInvokers.firstEntry();
            return entry.getValue();
        }
    }
}
