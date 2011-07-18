package kyototycoon.netty;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class TsvRpcClientCodec implements ChannelUpstreamHandler, ChannelDownstreamHandler {
    private final TsvRpcRequestEncoder encoder = new TsvRpcRequestEncoder();
    private final TsvRpcResponseDecoder decoder = new TsvRpcResponseDecoder();

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        decoder.handleUpstream(ctx, e);
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        encoder.handleDownstream(ctx, e);
    }
}
