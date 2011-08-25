package kyototycoon;

public interface Cursor {
    void close();

    void jump();
    void jump(Object key);
    void jumpBack();
    void jumpBack(Object key);
    boolean step();
    boolean stepBack();
    void setValue(Object value);
    void setValue(Object value, ExpirationTime xt, boolean step);
    boolean remove();
    Object getKey();
    Object getKey(boolean step);
    Object getValue();
    Object getValue(boolean step);
    Record get();
    Record get(boolean step);
    Record seize();
}
