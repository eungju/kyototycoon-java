package kyototycoon.netty;

import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TsvRpcCall {
    private final CountDownLatch latch;
    public final TsvRpcRequest request;
    public TsvRpcResponse response;

    public TsvRpcCall(TsvRpcRequest request) {
        this.request = request;
        latch = new CountDownLatch(1);
    }

    public TsvRpcCall awaitUninterruptibly() {
        try {
            latch.await(1000, TimeUnit.MILLISECONDS);
            return this;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void completed(TsvRpcResponse response) {
        this.response = response;
        latch.countDown();
    }
}
