package github.ch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    KYRO((byte) 1, "kyro"),
    PROTOSTUFF((byte) 2, "protostuff");

    private final byte code;
    private final String name;
}
