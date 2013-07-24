package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;

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
        ChannelBuffer recvBuffer = ChannelBuffers.dynamicBuffer();
        while (true) {
            byte[] b = new byte[8 * 1024];
            int n = recv.read(b);
            recvBuffer.writeBytes(b, 0, n);
            recvBuffer.markReaderIndex();
            try {
                StatusLine statusLine = parseStatusLine(recvBuffer);
                Headers headers = parseHeaders(recvBuffer);
                ChannelBuffer body = null;
                if (headers.hasContentLength()) {
                    if (recvBuffer.readableBytes() < headers.getContentLength()) {
                        throw new BufferUnderflowException();
                    }
                    body = ChannelBuffers.buffer(headers.getContentLength());
                    recvBuffer.readBytes(body);
                }
                return new HttpResponse(statusLine, headers, body);
            } catch (BufferUnderflowException e) {
                recvBuffer.resetReaderIndex();
            }
        }
    }

    StatusLine parseStatusLine(ChannelBuffer buffer) throws BufferUnderflowException {
        String version = readAsStringUntil(buffer, SPACE);
        String code = readAsStringUntil(buffer, SPACE);
        String reason = readAsStringUntil(buffer, CRLF);
        return new StatusLine(version, Integer.parseInt(code), reason);
    }

    Headers parseHeaders(ChannelBuffer buffer) throws BufferUnderflowException {
        Headers headers = new Headers();
        while (true) {
            int n = buffer.bytesBefore(new BytesChannelBufferIndexFinder(CRLF));
            if (n == 0) {
                buffer.skipBytes(CRLF.length);
                return headers;
            }
            String name = readAsStringUntil(buffer, COLON);
            buffer.skipBytes(buffer.bytesBefore(ChannelBufferIndexFinder.NOT_LINEAR_WHITESPACE));
            String value = readAsStringUntil(buffer, CRLF);
            headers.addHeader(name, value);
        }
    }

    String readAsStringUntil(ChannelBuffer buffer, byte value) throws BufferUnderflowException {
        int n = buffer.bytesBefore(value);
        if (n == -1) {
            throw new BufferUnderflowException();
        }
        ChannelBuffer d = buffer.readSlice(n);
        buffer.skipBytes(1);
        return d.toString(Charset.defaultCharset());
    }

    static class BytesChannelBufferIndexFinder implements ChannelBufferIndexFinder {
        private final byte[] value;

        public BytesChannelBufferIndexFinder(byte[] value) {
            this.value = value;
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            if (buffer.capacity() <= guessedIndex + value.length) {
                return false;
            }
            for (int i = 0; i < value.length; i++) {
                byte b = buffer.getByte(guessedIndex + i);
                if (b != value[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    String readAsStringUntil(ChannelBuffer buffer, byte[] value) throws BufferUnderflowException {
        int n = buffer.bytesBefore(new BytesChannelBufferIndexFinder(value));
        if (n == -1) {
            throw new BufferUnderflowException();
        }
        ChannelBuffer d = buffer.readSlice(n);
        buffer.skipBytes(value.length);
        return d.toString(Charset.defaultCharset());
    }
}
