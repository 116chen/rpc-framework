package github.ch.extension;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/3/2 下午8:17
 */
public class Holder<T> {
    private volatile T data;

    public T get() {
        return data;
    }

    public void set(T data) {
        this.data = data;
    }
}
