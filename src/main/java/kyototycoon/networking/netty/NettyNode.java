package kyototycoon.networking.netty;

import kyototycoon.tsv.Values;
import org.jboss.netty.bootstrap.ClientBootstrap;

import java.net.URI;

public class NettyNode {
    private final ClientBootstrap bootstrap;
    private URI address;
    private NettyTsvRpcConnection connection;

    public NettyNode(ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void initialize(URI address) {
        this.address = address;
    }

    public void start() {
        connection = new NettyTsvRpcConnection(bootstrap);
        connection.open(address);
    }

    public void stop() {
        connection.close();
    }

    public Values call(String procedure, Values input) {
        Values output = connection.call(procedure, input);
        return output;
    }
}
