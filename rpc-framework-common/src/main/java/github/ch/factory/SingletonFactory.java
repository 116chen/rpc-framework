package github.ch.factory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/24 下午10:22
 */
public class SingletonFactory {
    private final static Map<String, Object> SINGLETON_FACTORY = new HashMap<>();

    public static <T> T getInstance(Class<T> tClass) {
        String key = tClass.toString();
        T obj;
        synchronized (SingletonFactory.class) {
            try {
                if (SINGLETON_FACTORY.containsKey(key))
                    obj = tClass.cast(SINGLETON_FACTORY.get(key));
                else {
                    Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
                    obj = declaredConstructor.newInstance();
                    SINGLETON_FACTORY.put(key, obj);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return obj;
    }
}
