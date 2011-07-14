package kyototycoon.finagle;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.twitter.finagle.Service;
import com.twitter.finagle.ServiceFactory;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import kyototycoon.TsvRpcClient;
import kyototycoon.TsvRpcConnection;
import kyototycoon.netty.TsvRpcClientCodec;
import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class FinagleTsvRpcClient implements TsvRpcClient {
    private Iterable<URI> addresses;
    private ServiceFactory<HttpRequest, HttpResponse> serviceFactory;
    private Service<HttpRequest, HttpResponse> service;

    public void setHosts(Iterable<URI> addresses) {
        this.addresses = addresses;
    }

    public void start() {
        String hosts = Joiner.on(",").join(Iterables.transform(addresses, new Function<URI, String>() {
            public String apply(URI input) {
                return input.getHost() + ":" + input.getPort();
            }
        }));
        ClientBuilder builder = ClientBuilder.get()
                        .codec(Http.get())
                        .hosts(hosts)
                        .hostConnectionLimit(100)
                        .connectionTimeout(Duration.fromTimeUnit(1, TimeUnit.SECONDS))
                        .requestTimeout(Duration.fromTimeUnit(1, TimeUnit.SECONDS));
                        //.retries(2);
                        //.reportTo(new OstrichStatsReceiver())
                        //.logger(Logger.getLogger("http"));
        serviceFactory = ClientBuilder.safeBuildFactory(builder);
        service = serviceFactory.service();
    }

    public void stop() {
        serviceFactory.close();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        Future<HttpResponse> future = service.apply(TsvRpcClientCodec.encodeRequest(request));
        TsvRpcResponse response = TsvRpcClientCodec.decodeResponse(future.apply());
        return response;
    }

    public TsvRpcConnection getConnection() {
        return new FinagleTsvRpcConnection(serviceFactory.make().apply());
    }
}
