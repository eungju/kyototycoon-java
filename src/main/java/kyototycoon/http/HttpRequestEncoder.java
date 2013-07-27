package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class HttpRequestEncoder implements HttpMessageCoder {
    private final ChannelBuffer buffer;
    private final Charset charset = Charset.defaultCharset();

    public HttpRequestEncoder() {
        buffer = ChannelBuffers.dynamicBuffer();
    }

    public void writeTo(OutputStream output) throws IOException {
        buffer.readBytes(output, buffer.readableBytes());
    }

    public void encode(HttpRequest request) {
        requestLine(request.requestLine);
        headers(request.headers);
        if (request.body != null) {
            bytes(request.body);
        }
    }

    void requestLine(RequestLine requestLine) {
        string(requestLine.method);
        sp();
        string(requestLine.uri);
        sp();
        string(requestLine.version);
        crlf();
    }

    void headers(Headers headers) {
        for (Header header : headers) {
            header(header);
        }
        crlf();
    }

    void header(Header header) {
        string(header.name);
        colon();
        sp();
        string(header.value);
        crlf();
    }

    void bytes(ChannelBuffer value) {
        buffer.writeBytes(value);
    }

    void string(String value) {
        buffer.writeBytes(value.getBytes(charset));
    }

    void sp() {
        buffer.writeBytes(SP);
    }

    void colon() {
        buffer.writeBytes(COLON);
    }

    void crlf() {
        buffer.writeBytes(CRLF);
    }
}
