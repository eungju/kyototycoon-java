package kyototycoon;

import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;

public interface TsvRpcConnection {
    TsvRpcResponse call(TsvRpcRequest request);
    void close();
}
