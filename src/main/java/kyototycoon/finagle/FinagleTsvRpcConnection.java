package kyototycoon.finagle;

import com.twitter.finagle.Service;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class FinagleTsvRpcConnection extends FinagleTsvRpc implements TsvRpcConnection {
    public FinagleTsvRpcConnection(Service<HttpRequest, HttpResponse> service) {
        this.service = service;
    }

    public void close() {
        service.release();
    }
}
