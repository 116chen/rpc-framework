package github.ch.remoting.transport;

import github.ch.extension.SPI;
import github.ch.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    Object sendRequest(RpcRequest rpcRequest);
}
