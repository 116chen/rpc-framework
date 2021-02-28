package github.ch.entity;

import lombok.*;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/28 下午5:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SimpleEntity {
    private String sendMessage;
    private String receiveMessage;
}
