package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class HttpResponseDecoder {
    private final ChannelBuffer buffer;
    private final Charset charset = Charset.defaultCharset();
    private static final byte[] SP = " ".getBytes();
    private static final byte[] COLON = ":".getBytes();
    private static final byte[] CRLF = "\r\n".getBytes();

    public HttpResponseDecoder() {
        this.buffer = ChannelBuffers.dynamicBuffer();
    }

    public void fill(InputStream input) throws IOException {
        byte[] b = new byte[4 * 1024];
        int n = input.read(b);
        fill(b, 0, n);
    }

    public void fill(byte[] input) {
        fill(input, 0, input.length);
    }

    public void fill(byte[] input, int index, int length) {
        buffer.writeBytes(input, index, length);
    }

    public HttpResponse decode() {
        buffer.markReaderIndex();
        try {
            StatusLine statusLine = statusLine();
            Headers headers = headers();
            ChannelBuffer body = null;
            if (headers.hasContentLength()) {
                if (buffer.readableBytes() < headers.getContentLength()) {
                    throw new UnderflowDecoderException();
                }
                body = ChannelBuffers.buffer(headers.getContentLength());
                buffer.readBytes(body);
            }
            return new HttpResponse(statusLine, headers, body);
        } catch (UnderflowDecoderException e) {
            buffer.resetReaderIndex();
            return null;
        }
    }

    StatusLine statusLine() throws UnderflowDecoderException {
        String version = stringEndsWith(SP);
        String code = stringEndsWith(SP);
        String reason = stringEndsWith(CRLF);
        return new StatusLine(version, Integer.parseInt(code), reason);
    }

    Headers headers() throws UnderflowDecoderException {
        Headers headers = new Headers();
        while (true) {
            if (expect(CRLF)) {
                return headers;
            }
            Header header = header();
            headers.addHeader(header);
        }
    }

    Header header() throws UnderflowDecoderException {
        String name = stringEndsWith(COLON);
        ignoreWhiteSpaces();
        String value = stringEndsWith(CRLF);
        return new Header(name, value);
    }

    String stringEndsWith(byte[] value) throws UnderflowDecoderException {
        int n = buffer.bytesBefore(new BytesChannelBufferIndexFinder(value));
        if (n == -1) {
            throw new UnderflowDecoderException();
        }
        ChannelBuffer d = buffer.readSlice(n);
        buffer.skipBytes(value.length);
        return d.toString(charset);
    }

    void ignoreWhiteSpaces() throws UnderflowDecoderException {
        int n = buffer.bytesBefore(ChannelBufferIndexFinder.NOT_LINEAR_WHITESPACE);
        if (n == -1) {
            throw new UnderflowDecoderException();
        }
        buffer.skipBytes(n);
    }

    boolean expect(byte[] value) throws UnderflowDecoderException {
        int n = buffer.bytesBefore(new BytesChannelBufferIndexFinder(value));
        if (n == -1) {
            throw new UnderflowDecoderException();
        } else if (n == 0) {
            buffer.skipBytes(value.length);
            return true;
        }
        return false;
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
}
