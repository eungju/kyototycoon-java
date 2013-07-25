package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HttpRequestEncoderTest {
    @Test
    public void encode() throws Exception {
        HttpRequestEncoder dut = new HttpRequestEncoder();
        Headers headers = new Headers();
        headers.addHeader("Content-Length", "14");
        dut.encode(new HttpRequest(new RequestLine("POST", "/", "HTTP/1.1"), headers, ChannelBuffers.wrappedBuffer("Hello, World\r\n".getBytes())));
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        dut.writeTo(actual);
        assertThat(actual.toByteArray(), is("POST / HTTP/1.1\r\nContent-Length: 14\r\n\r\nHello, World\r\n".getBytes()));
    }
}
