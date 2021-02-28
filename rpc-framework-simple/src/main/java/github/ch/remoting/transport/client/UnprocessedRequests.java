package github.ch.remoting.transport.client;


import github.ch.remoting.dto.RpcResponse;
import io.netty.util.concurrent.CompleteFuture;


import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午10:55
 */
public class UnprocessedRequests {
    private final static Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_REQUESTS_MAP = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> completableFuture) {
        UNPROCESSED_REQUESTS_MAP.put(requestId, completableFuture);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQUESTS_MAP.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
