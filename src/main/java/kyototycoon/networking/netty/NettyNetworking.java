package kyototycoon.networking.netty;

import kyototycoon.tsv.Values;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

public class NettyNetworking {
    private ClientBootstrap bootstrap;
    private URI[] addresses;
    private Channel channel;

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
        URI address = addresses[0];
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(address.getHost(), address.getPort()));
        channel = future.awaitUninterruptibly().getChannel();
    }

    public void stop() {
        channel.close();
        bootstrap.releaseExternalResources();
    }

    public Values call(String procedure, Values input) {
        TsvRpcCall call = new TsvRpcCall(new TsvRpcRequest(procedure, input));
        channel.write(call);
        return call.awaitUninterruptibly().response.output;
    }
}
