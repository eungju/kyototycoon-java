package kyototycoon.finagle;

import com.twitter.finagle.Codec;
import com.twitter.finagle.Codec$class;
import com.twitter.finagle.ServiceFactory;
import kyototycoon.netty.TsvRpcClientCodec;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

public class FinagleTsvRpcCodec implements Codec<TsvRpcRequest, TsvRpcResponse> {
    public ChannelPipelineFactory pipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("http", new HttpClientCodec(4096, 8192, Integer.MAX_VALUE));
                pipeline.addLast("tsvrpc", new TsvRpcClientCodec());
                return pipeline;
            }
        };
    }

    public ServiceFactory<TsvRpcRequest, TsvRpcResponse>prepareServiceFactory(ServiceFactory<TsvRpcRequest, TsvRpcResponse> underlying) {
        return Codec$class.prepareServiceFactory(this, underlying);
    }

    public ServiceFactory<TsvRpcRequest, TsvRpcResponse> prepareConnFactory(ServiceFactory<TsvRpcRequest, TsvRpcResponse> underlying) {
        return Codec$class.prepareConnFactory(this, underlying);
    }

    public ServiceFactory<TsvRpcRequest, TsvRpcResponse> rawPrepareClientConnFactory(ServiceFactory<Object, Object> underlying) {
        return Codec$class.rawPrepareClientConnFactory(this, underlying);
    }

    public  ServiceFactory<Object, Object> rawPrepareServerConnFactory(ServiceFactory<TsvRpcRequest, TsvRpcResponse> underlying) {
        return Codec$class.rawPrepareServerConnFactory(this, underlying);
    }
}
