package kyototycoon.finagle;

import com.twitter.finagle.Codec;
import com.twitter.finagle.Codec$class;
import com.twitter.finagle.Service;
import com.twitter.util.Future;
import kyototycoon.netty.TsvRpcClientCodec;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

public class FinagleTsvRpcCodec implements Codec<TsvRpcRequest, TsvRpcResponse> {
    public Future<Service<TsvRpcRequest, TsvRpcResponse>> prepareService(Service<TsvRpcRequest, TsvRpcResponse> underlying) {
        //return Future.value(underlying);
        return Codec$class.prepareService(this, underlying);
    }

    public ChannelPipelineFactory pipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("http", new HttpClientCodec());
                pipeline.addLast("tsvrpc", new TsvRpcClientCodec());
                return pipeline;
            }
        };
    }
}