package kyototycoon.finagle;

import com.twitter.finagle.Service;
import com.twitter.util.Future;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;

public abstract class FinagleTsvRpc implements TsvRpc {
    protected Service<TsvRpcRequest, TsvRpcResponse> service;

    public TsvRpcResponse call(TsvRpcRequest request) {
        Future<TsvRpcResponse> future = service.apply(request);
        TsvRpcResponse response = future.apply();
        return response;
    }
}
