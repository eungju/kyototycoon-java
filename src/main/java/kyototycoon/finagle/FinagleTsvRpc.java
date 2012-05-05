package kyototycoon.finagle;

import com.twitter.finagle.Service;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FinagleTsvRpc implements TsvRpc {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    protected Service<TsvRpcRequest, TsvRpcResponse> service;

    public TsvRpcResponse call(TsvRpcRequest request) {
        com.twitter.util.Future<TsvRpcResponse> future = service.apply(request);
        TsvRpcResponse response = future.apply();
        logger.debug("Request: {}, Response: {}", request, response);
        return response;
    }
}
