package kyototycoon.netty;

import kyototycoon.tsvrpc.Assoc;
import kyototycoon.tsvrpc.TsvEncoding;
import kyototycoon.tsvrpc.TsvEncodingHelper;
import kyototycoon.tsvrpc.TsvRpcResponse;
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
        return decode(httpResponse);
    }

    public static TsvRpcResponse decode(HttpResponse httpResponse) {
        HttpResponseStatus status = httpResponse.getStatus();
        TsvEncoding tsvEncoding = TsvEncodingHelper.forContentType(httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE));
        ChannelBuffer content = httpResponse.getContent();
        Assoc output = tsvEncoding.decode(content);
        return new TsvRpcResponse(status.getCode(), output);
    }
}
