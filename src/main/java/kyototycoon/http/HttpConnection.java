package kyototycoon.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class HttpConnection {
    private final Socket socket;
    private final OutputStream send;
    private final InputStream recv;
    private boolean keepAlive;

    public HttpConnection(SocketAddress address, int timeout) throws Exception {
        socket = new Socket();
        socket.setSoTimeout(timeout);
        socket.connect(address);
        send = socket.getOutputStream();
        recv = socket.getInputStream();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public HttpResponse execute(HttpRequest request) throws IOException {
        //send
        keepAlive = request.headers.isConnectionKeepAlive();
        HttpRequestEncoder encoder = new HttpRequestEncoder();
        encoder.encode(request);
        encoder.writeTo(send);
        //receive
        HttpResponseDecoder decoder = new HttpResponseDecoder();
        while (true) {
            decoder.readFrom(recv);
            HttpResponse response = decoder.decode();
            if (response != null) {
                keepAlive = response.headers.isConnectionKeepAlive();
                return response;
            }
        }
    }
}
