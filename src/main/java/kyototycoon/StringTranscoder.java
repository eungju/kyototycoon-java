package kyototycoon;

import java.nio.charset.Charset;

public class StringTranscoder implements Transcoder {
    private final Charset encoding = Charset.forName("UTF-8");
    public static final StringTranscoder INSTANCE = new StringTranscoder();

    public byte[] encode(Object decoded) {
        return ((String) decoded).getBytes(encoding);
    }

    public Object decode(byte[] encoded) {
        return new String(encoded, encoding);
    }
}
