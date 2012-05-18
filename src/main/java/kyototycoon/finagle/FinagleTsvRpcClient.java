package kyototycoon.finagle;

import com.twitter.finagle.ServiceFactory;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.StorageUnit;
import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import scala.collection.JavaConversions;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FinagleTsvRpcClient extends FinagleTsvRpc implements TsvRpcClient {
    private URI address;
	private Duration requestTimeout = null;
    private ServiceFactory<HttpRequest, HttpResponse> serviceFactory;

    public void setHost(URI address) {
        this.address = address;
    }
    
    public void setRequestTimeout(long timeout, TimeUnit unit) {
    	this.requestTimeout = Duration.fromTimeUnit(timeout, unit);
    }

    public void start() {
        serviceFactory = buildServiceFactory();
        service = serviceFactory.toService();
    }

    ServiceFactory<HttpRequest, HttpResponse> buildServiceFactory() {
        ClientBuilder builder = ClientBuilder.get()
                .codec(Http.get().maxRequestSize(new StorageUnit(1024 * 1024 * 1024)).maxResponseSize(new StorageUnit(1024 * 1024 * 1024)))
                .hostConnectionLimit(100);
        if (requestTimeout != null) {
            builder = builder.requestTimeout(requestTimeout);
        }
        if (address.getScheme().equals("http")) {
            builder = builder.hosts(new InetSocketAddress(address.getHost(), address.getPort()));
        } else if (address.getScheme().equals("lb")) {
            List<SocketAddress> addresses = new ArrayList<SocketAddress>();
            for (URI uri : getComponents(address)) {
                addresses.add(new InetSocketAddress(uri.getHost(), uri.getPort()));
            }
            builder = builder.hosts(JavaConversions.asScalaBuffer(addresses));
        } else {
            throw new IllegalStateException("Unknown uri scheme " + address);
        }
        return ClientBuilder.safeBuildFactory(builder);
    }

    public void stop() {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }

    public TsvRpcConnection getConnection() {
        return new FinagleTsvRpcConnection(serviceFactory.apply().apply());
    }

    public List<URI> getComponents(URI uri) {
        List<URI> children = new ArrayList<URI>();
        String part = uri.getSchemeSpecificPart();
        for (String each : part.substring(1, part.length() - 1).split(",")) {
            children.add(URI.create(each));
        }
        return children;
    }
}
