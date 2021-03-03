package github.ch.serialize;

import github.ch.extension.SPI;

@SPI
public interface Serializer {
    /**
     * 序列化
     *
     * @param object 序列化的对象
     * @return 序列化后的数组
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     *
     * @param bytes 反序列化的字节数组
     * @param cla   反序列化后的Class类型
     */
    <T> T deserialize(byte[] bytes, Class<T> cla);
}
