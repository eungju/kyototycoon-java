package kyototycoon.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

public class TsvRpcClientPipelineFactory implements ChannelPipelineFactory {
    private static final int MAX_CONTENT_LENGTH = 1024 * 1024;

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("http", new HttpClientCodec());
        pipeline.addLast("http-aggregator", new HttpChunkAggregator(MAX_CONTENT_LENGTH));
        pipeline.addLast("tsvrpc", new TsvRpcClientCodec());
        pipeline.addLast("tsvrpc-handler", new TsvRpcClientHandler());
        return pipeline;
    }
}
