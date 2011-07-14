package kyototycoon.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

public class TsvRpcClientPipelineFactory implements ChannelPipelineFactory {
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("http", new HttpClientCodec());
        //pipeline.addLast("http-aggregator", new HttpChunkAggregator(1024 * 1024));
        pipeline.addLast("tsvrpc", new TsvRpcClientCodec());
        pipeline.addLast("handler", new TsvRpcClientHandler());
        return pipeline;
    }
}
