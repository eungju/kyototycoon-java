package kyototycoon;

import java.util.Map;

public interface Cursor {
    void close();

    void jump();
    void jump(Object key);
    void jumpBack();
    void jumpBack(Object key);
    boolean step();
    boolean stepBack();
    Object getKey();
    Object getKey(boolean step);
    Map.Entry<Object,Object> get(boolean step);
}
