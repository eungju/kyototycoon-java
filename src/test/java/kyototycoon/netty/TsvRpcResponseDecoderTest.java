package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TsvRpcResponseDecoderTest {
    TsvRpcResponseDecoder dut;
    ChannelHandlerContext ctx;
    Channel channel;

    @Before public void beforeEach() {
        dut = new TsvRpcResponseDecoder();
    }

    @Test
    public void decode() throws Exception {
        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "Ok"));
        httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/tab-separated-values");
        httpResponse.setContent(ChannelBuffers.wrappedBuffer("key\tvalue\r\n".getBytes()));
        TsvRpcResponse actual = (TsvRpcResponse) dut.decode(ctx, channel, httpResponse);
        assertThat(actual.status, is(200));
        assertThat(actual.output, is(new Values().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void skipIfTheMessageIsNotAnHttpResponse() throws Exception {
        Object msg = "message";
        assertThat(dut.decode(ctx, channel, msg), is(msg));
    }
}
