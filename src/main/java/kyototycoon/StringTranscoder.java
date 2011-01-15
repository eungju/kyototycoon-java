package kyototycoon;

import java.nio.charset.Charset;

public class StringTranscoder implements Transcoder<String> {
    private final Charset encoding = Charset.forName("UTF-8");
    public static final StringTranscoder INSTANCE = new StringTranscoder();

    public byte[] encode(String decoded) {
        return ((String) decoded).getBytes(encoding);
    }

    public String decode(byte[] encoded) {
        return new String(encoded, encoding);
    }
}
