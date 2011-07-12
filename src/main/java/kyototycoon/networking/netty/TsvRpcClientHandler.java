package kyototycoon.networking.netty;

import kyototycoon.tsv.TsvRpcResponse;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TsvRpcClientHandler extends SimpleChannelHandler {
    private final BlockingQueue<TsvRpcCall> waiting = new LinkedBlockingQueue<TsvRpcCall>();

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        TsvRpcCall call = (TsvRpcCall) e.getMessage();
        waiting.offer(call);
        Channels.write(ctx, e.getFuture(), call.request);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        TsvRpcCall call = waiting.poll();
        TsvRpcResponse response = (TsvRpcResponse) e.getMessage();
        call.completed(response);
    }
}
