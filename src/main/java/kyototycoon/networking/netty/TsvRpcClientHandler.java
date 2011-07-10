package kyototycoon.networking.netty;

import kyototycoon.tsv.TsvEncoding;
import kyototycoon.tsv.TsvEncodingHelper;
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

import java.util.ArrayDeque;
import java.util.Queue;

public class TsvRpcClientHandler extends SimpleChannelHandler {
    HttpRequest encodeRequest(TsvRpcRequest request) {
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

    TsvRpcResponse decodeResponse(HttpResponse httpResponse) {
        HttpResponseStatus status = httpResponse.getStatus();
        TsvEncoding tsvEncoding = TsvEncodingHelper.forContentType(httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE));
        ChannelBuffer content = httpResponse.getContent();
        byte[] buf = new byte[content.readableBytes()];
        content.readBytes(buf);
        Values output = tsvEncoding.decode(buf);
        return new TsvRpcResponse(output);
    }

    private Queue<TsvRpcCall> waiting = new ArrayDeque<TsvRpcCall>();

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        TsvRpcCall call = (TsvRpcCall) e.getMessage();
        HttpRequest httpRequest = encodeRequest(call.request);
        waiting.add(call);
        Channels.write(ctx, e.getFuture(), httpRequest);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpResponse httpResponse = (HttpResponse) e.getMessage();
        TsvRpcResponse response = decodeResponse(httpResponse);
        TsvRpcCall call = waiting.remove();
        call.completed(response);
    }
}
