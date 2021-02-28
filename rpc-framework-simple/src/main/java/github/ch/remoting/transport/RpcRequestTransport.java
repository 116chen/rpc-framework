package github.ch.remoting.transport;

import github.ch.remoting.dto.RpcRequest;

public interface RpcRequestTransport {
    Object sendRequest(RpcRequest rpcRequest);
}
