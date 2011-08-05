package kyototycoon.tsvrpc;

import org.apache.commons.codec.binary.Base64;

public class Base64ValueEncoding extends CommonsCodecValueEncoding {
    private static final Base64 codec = new Base64();

    public Base64ValueEncoding() {
        super(codec, codec);
    }
}
