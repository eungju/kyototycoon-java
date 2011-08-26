package kyototycoon;

public interface Cursor {
    void close();

    boolean jump();
    boolean jump(Object key);
    boolean jumpBack();
    boolean jumpBack(Object key);
    boolean step();
    boolean stepBack();
    boolean setValue(Object value);
    boolean setValue(Object value, ExpirationTime xt, boolean step);
    boolean remove();
    Object getKey();
    Object getKey(boolean step);
    Object getValue();
    Object getValue(boolean step);
    Record get();
    Record get(boolean step);
    Record seize();
}
