package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvEncoding;
import kyototycoon.tsvrpc.TsvEncodingHelper;
import kyototycoon.tsvrpc.TsvRpcRequest;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class TsvRpcRequestEncoder extends OneToOneEncoder {
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof TsvRpcRequest)) {
            return msg;
        }
        TsvRpcRequest request = (TsvRpcRequest) msg;
        return encode(request);
    }

    public static HttpRequest encode(TsvRpcRequest request) {
        HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/rpc/" + request.procedure);
        httpRequest.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        TsvEncoding tsvEncoding = TsvEncodingHelper.forEfficiency(request.input);
        ChannelBuffer content = tsvEncoding.encode(request.input);
        httpRequest.setHeader(HttpHeaders.Names.CONTENT_TYPE, tsvEncoding.contentType);
        httpRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        httpRequest.setContent(content);
        return httpRequest;
    }
}
