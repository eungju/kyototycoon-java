package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.URI;
import java.util.concurrent.Executors;

public class NettyTsvRpcClient implements TsvRpcClient {
    private ClientBootstrap bootstrap;
    private Iterable<URI> addresses;
    private NettyTsvRpcConnection connection;

    public NettyTsvRpcClient() {
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new TsvRpcClientPipelineFactory());
    }

    public void setHosts(Iterable<URI> addresses) {
        this.addresses = addresses;
    }

    public void start() {
        connection = new NettyTsvRpcConnection(bootstrap, addresses.iterator().next());
    }

    public void stop() {
        connection.close();
        bootstrap.releaseExternalResources();
    }

    public TsvRpcConnection getConnection() {
        return new NettyTsvRpcConnection(bootstrap, addresses.iterator().next());
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        return connection.call(request);
    }
}
