package kyototycoon.netty;

import kyototycoon.tsv.TsvEncoding;
import kyototycoon.tsv.TsvEncodingHelper;
import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.TsvRpcResponse;
import kyototycoon.tsv.Values;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class TsvRpcClientCodec extends SimpleChannelHandler {
    public static HttpRequest encodeRequest(TsvRpcRequest request) {
        HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/rpc/" + request.procedure);
        httpRequest.setHeader(HttpHeaders.Names.HOST, "localhost:1978");
        httpRequest.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        TsvEncoding tsvEncoding = TsvEncodingHelper.forEfficiency(request.input);
        ChannelBuffer content = ChannelBuffers.wrappedBuffer(tsvEncoding.encode(request.input));
        httpRequest.setHeader(HttpHeaders.Names.CONTENT_TYPE, tsvEncoding.contentType);
        httpRequest.setHeader(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        httpRequest.setContent(content);
        return httpRequest;
    }

    public static TsvRpcResponse decodeResponse(HttpResponse httpResponse) {
        HttpResponseStatus status = httpResponse.getStatus();
        TsvEncoding tsvEncoding = TsvEncodingHelper.forContentType(httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE));
        ChannelBuffer content = httpResponse.getContent();
        byte[] buf = new byte[content.readableBytes()];
        content.readBytes(buf);
        Values output = tsvEncoding.decode(buf);
        return new TsvRpcResponse(status.getCode(), output);
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        TsvRpcRequest request = (TsvRpcRequest) e.getMessage();
        HttpRequest httpRequest = encodeRequest(request);
        Channels.write(ctx, e.getFuture(), httpRequest);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpResponse httpResponse = (HttpResponse) e.getMessage();
        TsvRpcResponse response = decodeResponse(httpResponse);
        Channels.fireMessageReceived(ctx, response);
    }
}
