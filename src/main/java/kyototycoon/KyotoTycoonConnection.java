package kyototycoon;

public interface KyotoTycoonConnection extends KyotoTycoonRpc {
    void close();

    Cursor cursor();

    void setSignalWaiting(String name);
    void setSignalWaiting(String name, double timeout);
    void setSignalSending(String name);
    void setSignalSending(String name, boolean broadcast);
}
