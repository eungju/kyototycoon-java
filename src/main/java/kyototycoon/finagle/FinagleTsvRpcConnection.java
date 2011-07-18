package kyototycoon.finagle;

import com.twitter.finagle.Service;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;

public class FinagleTsvRpcConnection extends FinagleTsvRpc implements TsvRpcConnection {
    public FinagleTsvRpcConnection(Service<TsvRpcRequest, TsvRpcResponse> service) {
        this.service = service;
    }

    public void close() {
        service.release();
    }
}
