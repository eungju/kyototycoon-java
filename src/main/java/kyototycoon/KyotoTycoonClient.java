package kyototycoon;

/**
 * TODO: Lifecycle?
 */
public interface KyotoTycoonClient extends KyotoTycoonRpc {
    void stop();

    KyotoTycoonConnection getConnection();
}
