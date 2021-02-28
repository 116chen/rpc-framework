package github.ch.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.ch.remoting.dto.RpcRequest;
import github.ch.serialize.Serializer;
import github.ch.serialize.SerializerException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/21 下午2:08
 */
public class KryoSerializer implements Serializer {

    /*
     * ThreadLocal是解决线程安全问题，它通过为每个线程提供一个独立的变量副本解决了变量并发访问的冲突问题。
     * 在很多情况下，ThreadLocal比直接使用synchronized同步机制解决线程安全问题更简单，更方便，且结果程序拥有更高的并发性。
     * Kryo序列化不是线程安全的
     * */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);

        //开启注册服务，为Class注册Id，但不保证同一个Class每一次注册的Id都相同
        //也就是说，同样的代码、同一个 Class ，在两台机器上的注册编号可能不一致；那么，一台机器序列化之后的结果，可能就无法在另一台机器上反序列化。
        //多台机器建议关闭注册
        kryo.setReferences(true);

        //关闭循环引用解决方案，但遇到循环引用，会报"栈内溢出"错误
        kryo.setRegistrationRequired(false);

        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output os = new Output(baos);) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(os, object);
            kryoThreadLocal.remove();
            return os.toBytes();
        } catch (Exception e) {
            throw new SerializerException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cla) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             Input input = new Input(bais)) {
            Kryo kryo = kryoThreadLocal.get();
            T object = kryo.readObject(input, cla);
            kryoThreadLocal.remove();
            return object;
        } catch (Exception e) {
            throw new SerializerException("反序列化失败");
        }
    }
}
