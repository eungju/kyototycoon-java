package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvEncoding;
import kyototycoon.tsvrpc.TsvEncodingHelper;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class TsvRpcResponseDecoder extends OneToOneDecoder {
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof HttpResponse)) {
            return msg;
        }
        HttpResponse httpResponse = (HttpResponse) msg;
        HttpResponseStatus status = httpResponse.getStatus();
        TsvEncoding tsvEncoding = TsvEncodingHelper.forContentType(httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE));
        ChannelBuffer content = httpResponse.getContent();
        byte[] buf = new byte[content.readableBytes()];
        content.readBytes(buf);
        Values output = tsvEncoding.decode(buf);
        return new TsvRpcResponse(status.getCode(), output);
    }
}
