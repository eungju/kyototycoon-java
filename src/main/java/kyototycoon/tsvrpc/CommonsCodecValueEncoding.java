package kyototycoon.tsvrpc;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import java.io.IOException;
import java.util.Arrays;

public class CommonsCodecValueEncoding  implements ValueEncoding {
    private final BinaryEncoder encoder;
    private final BinaryDecoder decoder;

    public CommonsCodecValueEncoding(BinaryEncoder encoder, BinaryDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public byte[] encode(byte[] value) throws IOException {
        try {
            return encoder.encode(value);
        } catch (EncoderException e) {
            throw new IOException("Unable to encode " + Arrays.toString(value), e);
        }
    }

    public byte[] decode(byte[] value) throws IOException {
        try {
            return decoder.decode(value);
        } catch (DecoderException e) {
            throw new IOException("Unable to decode " + Arrays.toString(value), e);
        }
    }
}