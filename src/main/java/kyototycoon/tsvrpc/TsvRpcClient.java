package kyototycoon.tsvrpc;

import java.net.URI;

/**
 * TODO: Lifecycle?
 */
public interface TsvRpcClient extends TsvRpc {
    void setHosts(Iterable<URI> addresses);
    void start();
    void stop();
    TsvRpcConnection getConnection();
}
