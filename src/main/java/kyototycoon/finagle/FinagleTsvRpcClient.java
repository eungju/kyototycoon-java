package kyototycoon.finagle;

import com.twitter.finagle.ServiceFactory;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.util.Duration;
import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Lifecycle?
 */
public class FinagleTsvRpcClient extends FinagleTsvRpc implements TsvRpcClient {
    private Iterable<URI> addresses;
    private ServiceFactory<TsvRpcRequest, TsvRpcResponse> serviceFactory;

    public void setHosts(Iterable<URI> addresses) {
        this.addresses = addresses;
    }

    public void start() {
        StringBuilder hosts = new StringBuilder();
        for (URI address : addresses) {
            hosts.append(',');
            hosts.append(address.getHost()).append(':').append(address.getPort());
        }
        ClientBuilder builder = ClientBuilder.get()
                        .codec(new FinagleTsvRpcCodec())
                        .hosts(hosts.substring(1))
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

    public TsvRpcConnection getConnection() {
        return new FinagleTsvRpcConnection(serviceFactory.make().apply());
    }
}
