package kyototycoon.netty;

import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.Values;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

public class NettyTsvRpcConnection {
    private final ClientBootstrap bootstrap;
    private Channel channel;

    public NettyTsvRpcConnection() {
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new TsvRpcClientPipelineFactory());
    }
    
    public NettyTsvRpcConnection(ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void open(URI address) {
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(address.getHost(), address.getPort()));
        if (future.awaitUninterruptibly().isSuccess()) {
            channel = future.getChannel();
        } else {
            
        }
    }

    public void close() {
        channel.close().awaitUninterruptibly();
    }

    public Values call(String procedure, Values input) {
        TsvRpcCall call = new TsvRpcCall(new TsvRpcRequest(procedure, input));
        channel.write(call);
        return call.awaitUninterruptibly().response.output;
    }
}
