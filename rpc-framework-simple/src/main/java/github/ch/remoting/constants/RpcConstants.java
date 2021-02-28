package github.ch.remoting.constants;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午11:53
 */
public class RpcConstants {
    public static final byte REQUEST_TYPE = 1;

    public static final byte RESPONSE_TYPE = 2;

    public static final byte HEART_REQUEST = 3;

    public static final byte HEART_RESPONSE = 4;

    public static final String PING = "ping";
    public static final String PONG = "pong";

    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    public static final byte[] MAGIC_CODE = new byte[]{(byte) 'c', (byte) 'r', (byte) 'p', (byte) 'c'};

    public static final byte VERSION = 1;
    public static final int VERSION_LENGTH = 1;

    public static final int HEAD_LENGTH = 16;

}
