package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;

public class HttpResponse {
    public final StatusLine statusLine;
    public final Headers headers;
    public final ChannelBuffer body;

    public HttpResponse(StatusLine statusLine, Headers headers, ChannelBuffer body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }
}
