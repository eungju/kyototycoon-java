package kyototycoon.tsvrpc;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Lifecycle?
 */
public interface TsvRpcClient extends TsvRpc {
    TsvRpcConnection getConnection();

    void setHosts(Iterable<URI> addresses);
    void setRequestTimeout(long timeout, TimeUnit unit);
    void start();
    void stop();
}
