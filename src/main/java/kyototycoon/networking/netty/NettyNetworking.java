package kyototycoon.networking.netty;

import kyototycoon.networking.Networking;
import kyototycoon.tsv.Values;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.URI;
import java.util.concurrent.Executors;

public class NettyNetworking implements Networking {
    private ClientBootstrap bootstrap;
    private URI[] addresses;
    private NettyNode[] nodes;

    public NettyNetworking() {
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new TsvRpcClientPipelineFactory());
    }

    public void initialize(URI[] addresses) {
        this.addresses = addresses;
    }

    public void start() {
        nodes = new NettyNode[addresses.length];
        for (int i = 0; i < nodes.length; i++) {
            NettyNode node = new NettyNode(bootstrap);
            node.initialize(addresses[i]);
            node.start();
            nodes[i] = node;
        }
    }

    public void stop() {
        for (NettyNode node : nodes) {
            node.stop();
        }
        bootstrap.releaseExternalResources();
    }

    public Values call(String procedure, Values input) {
        return nodes[0].call(procedure, input);
    }
}
