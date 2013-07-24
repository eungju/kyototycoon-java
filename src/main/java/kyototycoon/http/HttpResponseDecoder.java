package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;

import java.nio.charset.Charset;

public class HttpResponseDecoder {
    private final ChannelBuffer buffer;
    private final Charset charset = Charset.defaultCharset();
    private static final byte SP = ' ';
    private static final byte COLON = ':';
    private static final byte[] CRLF = "\r\n".getBytes();

    public HttpResponseDecoder(ChannelBuffer buffer) {
        this.buffer = buffer;
    }

    public StatusLine statusLine() throws UnderflowDecoderException {
        String version = readAsStringUntil(buffer, SP);
        String code = readAsStringUntil(buffer, SP);
        String reason = readAsStringUntil(buffer, CRLF);
        return new StatusLine(version, Integer.parseInt(code), reason);
    }

    public Header header() throws UnderflowDecoderException {
        String name = readAsStringUntil(buffer, COLON);
        buffer.skipBytes(buffer.bytesBefore(ChannelBufferIndexFinder.NOT_LINEAR_WHITESPACE));
        String value = readAsStringUntil(buffer, CRLF);
        return new Header(name, value);
    }

    public Headers headers() throws UnderflowDecoderException {
        Headers headers = new Headers();
        while (true) {
            int n = buffer.bytesBefore(new BytesChannelBufferIndexFinder(CRLF));
            if (n == 0) {
                buffer.skipBytes(CRLF.length);
                return headers;
            }
            Header header = header();
            headers.addHeader(header);
        }
    }

    String readAsStringUntil(ChannelBuffer buffer, byte value) throws UnderflowDecoderException {
        int n = buffer.bytesBefore(value);
        if (n == -1) {
            throw new UnderflowDecoderException();
        }
        ChannelBuffer d = buffer.readSlice(n);
        buffer.skipBytes(1);
        return d.toString(charset);
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

    String readAsStringUntil(ChannelBuffer buffer, byte[] value) throws UnderflowDecoderException {
        int n = buffer.bytesBefore(new BytesChannelBufferIndexFinder(value));
        if (n == -1) {
            throw new UnderflowDecoderException();
        }
        ChannelBuffer d = buffer.readSlice(n);
        buffer.skipBytes(value.length);
        return d.toString(charset);
    }
}
