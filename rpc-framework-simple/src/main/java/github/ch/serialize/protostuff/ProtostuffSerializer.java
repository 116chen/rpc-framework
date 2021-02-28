package github.ch.serialize.protostuff;

import github.ch.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/25 下午10:52
 */
public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);


    @Override
    public byte[] serialize(Object object) {
        Class<?> aClass = object.getClass();
        Schema schema = RuntimeSchema.getSchema(aClass);
        byte[] bytes = null;
        try {
            bytes = ProtostuffIOUtil.toByteArray(object, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cla) {
        Schema<T> schema = RuntimeSchema.getSchema(cla);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
