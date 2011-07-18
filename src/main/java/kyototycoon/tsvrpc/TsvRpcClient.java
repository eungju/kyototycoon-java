package kyototycoon.tsvrpc;

import java.net.URI;

public interface TsvRpcClient extends TsvRpc {
    void setHosts(Iterable<URI> addresses);
    void start();
    void stop();
    TsvRpcConnection getConnection();
}
