package kyototycoon.transcoder;

import java.nio.charset.Charset;

public class StringTranscoder implements Transcoder<String> {
    public static final StringTranscoder INSTANCE = new StringTranscoder();
    private final Charset encoding = Charset.forName("UTF-8");

    public byte[] encode(String decoded) {
        return decoded.getBytes(encoding);
    }

    public String decode(byte[] encoded) {
        return new String(encoded, encoding);
    }
}
