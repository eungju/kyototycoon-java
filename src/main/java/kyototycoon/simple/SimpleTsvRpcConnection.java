package kyototycoon.simple;

import kyototycoon.http.*;
import kyototycoon.tsvrpc.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;

public class SimpleTsvRpcConnection implements TsvRpcConnection {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpConnection connection;

    public SimpleTsvRpcConnection(URI address, int timeout) throws Exception {
        this.connection = new HttpConnection(new InetSocketAddress(address.getHost(), address.getPort()), timeout);
    }

    public void close() {
        connection.close();
    }

    public boolean isAlive() {
        return connection.isKeepAlive();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        try {
            logger.debug("Sending a request: {}", request);
            HttpRequest httpRequest = encode(request);
            HttpResponse httpResponse = connection.execute(httpRequest);
            TsvRpcResponse response = decode(httpResponse);
            logger.debug("Received a response: {}", response);
            return response;
        } catch (HttpException e) {
            throw new RuntimeException("Call failed: " + request, e);
        }
    }

    public static HttpRequest encode(TsvRpcRequest request) {
        Headers headers = new Headers();
        headers.setConnection("Keep-Alive");
        TsvEncoding tsvEncoding = TsvEncodingHelper.forEfficiency(request.input);
        ChannelBuffer content = tsvEncoding.encode(request.input);
        headers.setContentType(tsvEncoding.contentType);
        headers.setContentLength(content.readableBytes());
        HttpRequest httpRequest = new HttpRequest(new RequestLine("POST", "/rpc/" + request.procedure, "HTTP/1.1"), headers, content);
        return httpRequest;
    }


    public static TsvRpcResponse decode(HttpResponse httpResponse) {
        StatusLine status = httpResponse.statusLine;
        Headers headers = httpResponse.headers;
        TsvEncoding tsvEncoding = TsvEncodingHelper.forContentType(headers.getContentType());
        ChannelBuffer content = httpResponse.body;
        Assoc output = tsvEncoding.decode(content);
        return new TsvRpcResponse(status.code, output);
    }
}
