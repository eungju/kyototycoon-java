package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class HttpConnection {
    private final Socket socket;
    private final OutputStream send;
    private final InputStream recv;

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

    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte COLON = ':';
    private static final byte SPACE = ' ';

    public HttpResponse execute(HttpRequest request) throws IOException {
        //send
        ChannelBuffer sendBuffer = ChannelBuffers.dynamicBuffer();
        sendBuffer.writeBytes(request.requestLine.method.getBytes());
        sendBuffer.writeByte(SPACE);
        sendBuffer.writeBytes(request.requestLine.uri.getBytes());
        sendBuffer.writeByte(SPACE);
        sendBuffer.writeBytes(request.requestLine.version.getBytes());
        sendBuffer.writeBytes(CRLF);
        for (Header header : request.headers) {
            sendBuffer.writeBytes(header.name.getBytes());
            sendBuffer.writeByte(COLON);
            sendBuffer.writeByte(SPACE);
            sendBuffer.writeBytes(header.value.getBytes());
            sendBuffer.writeBytes(CRLF);
        }
        sendBuffer.writeBytes(CRLF);
        if (request.body != null) {
            sendBuffer.writeBytes(request.body);
        }
        sendBuffer.readBytes(send, sendBuffer.readableBytes());
        //receive
        HttpResponseDecoder decoder = new HttpResponseDecoder();
        while (true) {
            decoder.fill(recv);
            HttpResponse response = decoder.decode();
            if (response != null) {
                return response;
            }
        }
    }
}
