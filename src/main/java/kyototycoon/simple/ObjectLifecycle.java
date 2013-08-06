package kyototycoon.simple;

public interface ObjectLifecycle<T> {
    T create();
    void destroy(T o);
}
