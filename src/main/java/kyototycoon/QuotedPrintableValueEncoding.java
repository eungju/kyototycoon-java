package kyototycoon;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import java.io.IOException;

public class QuotedPrintableValueEncoding implements ValueEncoding {
    private final QuotedPrintableCodec codec = new QuotedPrintableCodec();

    public byte[] encode(byte[] value) throws IOException {
        return codec.encode(value);
    }

    public byte[] decode(byte[] value) throws IOException {
        try {
            return codec.decode(value);
        } catch (DecoderException e) {
            throw new IOException("Error while decoding " + value, e);
        }
    }
}
