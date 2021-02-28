package github.ch.remoting.dto;

import lombok.*;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午9:50
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RpcMessage {
    private byte messageType;
    private byte coderType;
    private byte compressType;
    private int requestId;
    private Object data;
}
