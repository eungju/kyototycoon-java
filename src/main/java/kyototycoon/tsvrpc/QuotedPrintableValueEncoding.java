package kyototycoon.tsvrpc;

import org.apache.commons.codec.net.QuotedPrintableCodec;

public class QuotedPrintableValueEncoding extends CommonsCodecValueEncoding {
    private static final QuotedPrintableCodec codec = new QuotedPrintableCodec();

    public QuotedPrintableValueEncoding() {
        super(codec, codec);
    }
}
