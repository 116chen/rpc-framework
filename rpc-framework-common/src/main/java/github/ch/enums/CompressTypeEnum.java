package github.ch.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressTypeEnum {
    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String name;
}
