package kyototycoon;

import java.util.Map;

public interface Cursor {
    void close();

    void jump();
    void jump(Object key);
    Map.Entry<Object,Object> get(boolean step);
}
