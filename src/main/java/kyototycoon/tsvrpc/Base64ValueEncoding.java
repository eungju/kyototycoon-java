package kyototycoon.tsvrpc;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

/**
 * TODO: optimize BASE64 encoding.
 */
public class Base64ValueEncoding implements ValueEncoding {
    public byte[] encode(byte[] value) throws IOException {
        return new Base64().encode(value);
    }

    public byte[] decode(byte[] value) throws IOException {
        return new Base64().decode(value);
    }
}
