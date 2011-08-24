package kyototycoon.tsvrpc;

import util.Base64;

import java.io.IOException;

public class Base64ValueEncoding implements ValueEncoding {
    public byte[] encode(byte[] value) throws IOException {
        return Base64.encodeToByte(value, false);
    }

    public byte[] decode(byte[] value) throws IOException {
        return Base64.decodeFast(value);
    }
}
