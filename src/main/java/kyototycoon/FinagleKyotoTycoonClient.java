package kyototycoon;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.twitter.finagle.Service;
import com.twitter.finagle.ServiceFactory;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import kyototycoon.networking.netty.TsvRpcClientCodec;
import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;
import kyototycoon.tsv.Values;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class FinagleKyotoTycoonClient {
    private Iterable<URI> addresses;
    private ServiceFactory<HttpRequest, HttpResponse> serviceFactory;

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
    }

    public void stop() {
        serviceFactory.close();
    }

    public Values call(String procedure, Values input) {
        Future<HttpResponse> future = serviceFactory.service().apply(TsvRpcClientCodec.encodeRequest(new TsvRpcRequest(procedure, input)));
        TsvRpcResponse response = TsvRpcClientCodec.decodeResponse(future.apply());
        return response.output;
    }

    public KyotoTycoonConnection getConnection() {
        return new KyotoTycoonConnection() {
            private final Service<HttpRequest, HttpResponse> service = serviceFactory.make().apply();

            public Values call(String procedure, Values input) {
                Future<HttpResponse> future = service.apply(TsvRpcClientCodec.encodeRequest(new TsvRpcRequest(procedure, input)));
                TsvRpcResponse response = TsvRpcClientCodec.decodeResponse(future.apply());
                return response.output;
            }

            public void close() {
                service.release();
            }
        };
    }
}
