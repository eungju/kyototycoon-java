package kyototycoon.tsvrpc;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Lifecycle?
 */
public interface TsvRpcClient extends TsvRpc {
    TsvRpcConnection getConnection();

    void setHost(URI address);
    void setRequestTimeout(long timeout, TimeUnit unit);
    void start();
    void stop();
}
