package kyototycoon;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

public class Base64ValueEncoding implements ValueEncoding {
    public byte[] encode(byte[] value) throws IOException {
        return Base64.encodeBase64(value);
    }

    public byte[] decode(byte[] value) throws IOException {
        return Base64.decodeBase64(value);
    }
}
