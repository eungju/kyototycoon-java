package kyototycoon.netty;

import kyototycoon.tsvrpc.TsvEncoding;
import kyototycoon.tsvrpc.TsvEncodingHelper;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.Values;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TsvRpcRequestEncoderTest {
    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();
    TsvRpcRequestEncoder dut;
    ChannelHandlerContext ctx;
    Channel channel;

    @Before public void beforeEach() {
        dut = new TsvRpcRequestEncoder();
        ctx = null;
        channel = mockery.mock(Channel.class);
        mockery.checking(new Expectations() {{
            allowing(channel).getRemoteAddress(); will(returnValue(new InetSocketAddress("localhost", 1978)));
        }});
    }

    @Test public void encode() throws Exception {
        TsvRpcRequest request = new TsvRpcRequest("echo", new Values().put("key".getBytes(), "value".getBytes()));
        HttpRequest actual = (HttpRequest) dut.encode(ctx, channel,  request);

        assertThat(actual.getMethod(), is(HttpMethod.POST));
        assertThat(actual.getUri(), is("/rpc/echo"));
        assertThat(actual.getProtocolVersion(), is(HttpVersion.HTTP_1_1));
        assertThat(actual.getHeader(HttpHeaders.Names.CONNECTION), is(HttpHeaders.Values.KEEP_ALIVE));
        TsvEncoding tsvEncoding = TsvEncodingHelper.forEfficiency(request.input);
        ChannelBuffer content = ChannelBuffers.wrappedBuffer(tsvEncoding.encode(request.input));
        assertThat(actual.getHeader(HttpHeaders.Names.CONTENT_TYPE), is(tsvEncoding.contentType));
        assertThat(actual.getHeader(HttpHeaders.Names.CONTENT_LENGTH), is(String.valueOf(content.readableBytes())));
        assertThat(actual.getContent(), is(content));
    }

    @Test public void skipIfTheMessageIsNotATsvRpcRequest() throws Exception {
        Object msg = "message";
        assertThat(dut.encode(ctx, channel, msg), is(msg));
    }
}
