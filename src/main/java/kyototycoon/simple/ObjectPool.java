package kyototycoon.simple;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ObjectPool<T> {
    private final ObjectLifecycle<T> factory;
    private final BlockingQueue<T> idle;
    private final BlockingQueue<T> busy;
    private final int min;
    private final int max;
    private final int timeout;

    public ObjectPool(ObjectLifecycle<T> factory, int min, int max, int timeout) {
        this.factory = factory;
        this.min = min;
        this.max = max;
        this.timeout = timeout;
        idle = new LinkedBlockingQueue<T>();
        busy = new LinkedBlockingQueue<T>();
        for (int i = 0; i < min; i++) {
            idle.add(factory.create());
        }
    }

    public void dispose() {
        while (idle.isEmpty()) {
            factory.destroy(idle.remove());
        }
    }

    public T acquire() {
        if (idle.isEmpty() && (idle.size() + busy.size()) < max) {
            idle.add(factory.create());
        }
        try {
            T o = idle.poll(timeout, TimeUnit.MILLISECONDS);
            if (o == null) {
                throw new RuntimeException("All objects are busy");
            }
            busy.add(o);
            return o;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void release(T o) {
        busy.remove(o);
        idle.add(o);
    }

    public void abandon(T o) {
        busy.remove(o);
        factory.destroy(o);
    }
}
