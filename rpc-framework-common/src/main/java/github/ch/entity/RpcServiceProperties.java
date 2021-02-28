package github.ch.entity;

import lombok.*;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午9:58
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RpcServiceProperties {
    private String serviceName;
    private String version;
    private String group;

    public String toRpcServiceName() {
        return serviceName + version + group;
    }
}
