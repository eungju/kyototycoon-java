package kyototycoon.tsvrpc;

import java.util.concurrent.Future;

public interface TsvRpc {
    TsvRpcResponse call(TsvRpcRequest request);
    Future<TsvRpcResponse> callAsync(TsvRpcRequest request);
}
