package kyototycoon.finagle;

import com.twitter.finagle.Service;
import com.twitter.util.Duration;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class FinagleTsvRpc implements TsvRpc {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    protected Service<TsvRpcRequest, TsvRpcResponse> service;

    public TsvRpcResponse call(TsvRpcRequest request) {
        com.twitter.util.Future<TsvRpcResponse> future = service.apply(request);
        TsvRpcResponse response = future.apply();
        logger.debug("Request: {}, Response: {}", request, response);
        return response;
    }

    public Future<TsvRpcResponse> callAsync(TsvRpcRequest request) {
        return new TwitterFuture(service.apply(request));
    }

    private static class TwitterFuture implements Future<TsvRpcResponse> {
        private final com.twitter.util.Future<TsvRpcResponse> future;

        public TwitterFuture(com.twitter.util.Future<TsvRpcResponse> future) {
            this.future = future;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            if (isDone()) {
                return false;
            }
            future.cancel();
            return true;
        }

        public boolean isCancelled() {
            return future.isCancelled();
        }

        public boolean isDone() {
            return future.isDefined() || future.isCancelled();
        }

        public TsvRpcResponse get() {
            return future.apply();
        }

        public TsvRpcResponse get(long timeout, TimeUnit unit) {
            return future.apply(Duration.fromTimeUnit(timeout, unit));
        }
    }
}
