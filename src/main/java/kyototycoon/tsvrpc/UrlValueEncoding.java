package kyototycoon.tsvrpc;

import org.apache.commons.codec.net.URLCodec;

public class UrlValueEncoding extends CommonsCodecValueEncoding {
    private static final URLCodec codec = new URLCodec("UTF-8");

    public UrlValueEncoding() {
        super(codec, codec);
    }
}
