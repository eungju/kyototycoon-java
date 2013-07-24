package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;

public class HttpRequest {
    public final RequestLine requestLine;
    public final Headers headers;
    public final ChannelBuffer body;

    public HttpRequest(RequestLine requestLine, Headers headers, ChannelBuffer body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }
}
