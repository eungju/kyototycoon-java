package kyototycoon.finagle;

import com.twitter.finagle.Service;
import com.twitter.util.Future;
import kyototycoon.TsvRpcConnection;
import kyototycoon.netty.TsvRpcClientCodec;
import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class FinagleTsvRpcConnection implements TsvRpcConnection {
    private final Service<HttpRequest, HttpResponse> service;

    public FinagleTsvRpcConnection(Service<HttpRequest, HttpResponse> service) {
        this.service = service;
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        Future<HttpResponse> future = service.apply(TsvRpcClientCodec.encodeRequest(request));
        TsvRpcResponse response = TsvRpcClientCodec.decodeResponse(future.apply());
        return response;
    }

    public void close() {
        service.release();
    }
}
