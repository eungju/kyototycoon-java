package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NettyTsvRpcClient implements TsvRpcClient {
    private final ClientBootstrap bootstrap;
    private URI address;
    private NettyTsvRpcConnection connection;

    public NettyTsvRpcClient() {
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new TsvRpcClientPipelineFactory());
    }

    public void setHost(URI address) {
        this.address = address;
    }

    public void setRequestTimeout(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    public void start() {
        connection = new NettyTsvRpcConnection(bootstrap, address);
    }

    public void stop() {
        connection.close();
        bootstrap.releaseExternalResources();
    }

    public TsvRpcConnection getConnection() {
        return new NettyTsvRpcConnection(bootstrap, address);
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        return connection.call(request);
    }

    public Future<TsvRpcResponse> callAsync(TsvRpcRequest request) {
        return connection.callAsync(request);
    }
}
