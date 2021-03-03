package github.ch.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/3/2 下午8:18
 */
@Slf4j
public class ExtensionLoader<T> {
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADER = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> cacheInstances;
    private final Holder<Map<String, Class<?>>> cacheClasses;

    public ExtensionLoader(Class<?> type) {
        this.type = type;
        this.cacheInstances = new ConcurrentHashMap<>();
        this.cacheClasses = new Holder<>();
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null)
            throw new IllegalArgumentException("extension type should not be null");
        if (!type.isInterface())
            throw new IllegalArgumentException("extension type must be an interface");
        if (type.getAnnotation(SPI.class) == null)
            throw new IllegalArgumentException("extension type must be annotated by @SPI");
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADER.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADER.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADER.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("extension name should not be null or empty");
        Holder<Object> holder = cacheInstances.get(name);
        if (holder == null) {
            cacheInstances.putIfAbsent(name, new Holder<>());
            holder = cacheInstances.get(name);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        Class<?> aClass = getExtensionClasses().get(name);
        if (aClass == null)
            throw new IllegalArgumentException("no extension of the name");
        T instance = (T) EXTENSION_INSTANCES.get(aClass);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(aClass, aClass.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(aClass);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classMap = cacheClasses.get();
        if (classMap == null) {
            synchronized (cacheClasses) {
                classMap = cacheClasses.get();
                if (classMap == null) {
                    classMap = new ConcurrentHashMap<>();
                    loadDirectory(classMap);
                    cacheClasses.set(classMap);
                }
            }
        }
        return classMap;
    }


    private void loadDirectory(Map<String, Class<?>> classMap) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        Enumeration<URL> urls = null;
        try {
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    loadResource(classLoader, classMap, urls.nextElement());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadResource(ClassLoader classLoader, Map<String, Class<?>> classMap, URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                final int end = line.indexOf('#');
                if (end > 0) {
                    line = line.substring(0, end);
                }
                line = line.trim();
                if (line.length() > 0) {
                    final int eqi = line.indexOf('=');
                    final String key = line.substring(0, eqi).trim();
                    final String value = line.substring(eqi + 1).trim();

                    if (key.length() > 0 && value.length() > 0) {
                        classMap.putIfAbsent(key, classLoader.loadClass(value));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
