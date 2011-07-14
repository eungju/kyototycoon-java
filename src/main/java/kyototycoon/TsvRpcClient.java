package kyototycoon;

import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;

import java.net.URI;

public interface TsvRpcClient {
    TsvRpcResponse call(TsvRpcRequest request);
    TsvRpcConnection getConnection();

    void setHosts(Iterable<URI> addresses);
    void start();
    void stop();
}
