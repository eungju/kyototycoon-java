package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public Future<TsvRpcResponse> callAsync(TsvRpcRequest request) {
        final TsvRpcCall call = new TsvRpcCall(request);
        channel.write(call);
        return new Future<TsvRpcResponse>() {
            public boolean cancel(boolean b) {
                //TODO
                return false;
            }

            public boolean isCancelled() {
                //TODO
                return false;
            }

            public boolean isDone() {
                //TODO
                return true;
            }

            public TsvRpcResponse get() throws InterruptedException, ExecutionException {
                call.awaitUninterruptibly();
                return call.response;
            }

            public TsvRpcResponse get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                call.awaitUninterruptibly();
                return call.response;
            }
        };
    }
}
