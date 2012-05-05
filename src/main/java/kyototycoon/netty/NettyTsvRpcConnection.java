package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.URI;

public class NettyTsvRpcConnection implements TsvRpcConnection {
    private final ClientBootstrap bootstrap;
    private Channel channel;

    public NettyTsvRpcConnection(ClientBootstrap bootstrap, URI address) {
        this.bootstrap = bootstrap;
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(address.getHost(), address.getPort()));
        if (future.awaitUninterruptibly().isSuccess()) {
            channel = future.getChannel();
        } else {

        }
    }

    public void close() {
        channel.close().awaitUninterruptibly();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        TsvRpcCall call = new TsvRpcCall(request);
        channel.write(call);
        return call.awaitUninterruptibly().response;
    }
}
