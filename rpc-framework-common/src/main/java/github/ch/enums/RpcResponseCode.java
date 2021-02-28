package github.ch.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcResponseCode {
    SUCCESS("remote called successfully!", 200),
    FAIL("remote called failed!", -1);

    private final String message;
    private final int code;
}
