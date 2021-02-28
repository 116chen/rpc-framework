package github.ch.remoting.dto;

import github.ch.entity.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/21 下午2:12
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 4863805751335427757L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] args;
    private Class<?>[] argsType;
    private String version;
    private String group;

    public RpcServiceProperties toRpcServiceProperties() {
        return RpcServiceProperties.builder()
                .serviceName(getInterfaceName())
                .version(getVersion())
                .group(getGroup())
                .build();
    }

}
