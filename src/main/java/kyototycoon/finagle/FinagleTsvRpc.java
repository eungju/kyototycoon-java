package kyototycoon.finagle;

import com.twitter.finagle.Service;
import kyototycoon.netty.TsvRpcRequestEncoder;
import kyototycoon.netty.TsvRpcResponseDecoder;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FinagleTsvRpc implements TsvRpc {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    protected Service<HttpRequest, HttpResponse> service;

    public TsvRpcResponse call(TsvRpcRequest request) {
        logger.debug("Sending a request: {}", request);
        HttpRequest httpRequest = TsvRpcRequestEncoder.encode(request);
        com.twitter.util.Future<HttpResponse> future = service.apply(httpRequest);
        HttpResponse httpResponse = future.apply();
        TsvRpcResponse response = TsvRpcResponseDecoder.decode(httpResponse);
        logger.debug("Received a response: {}", response);
        return response;
    }
}
