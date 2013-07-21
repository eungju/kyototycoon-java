package kyototycoon.tsvrpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class TsvEncodingHelperTest {
    @Test(expected=IllegalArgumentException.class)
    public void detectUnknownEncoding() {
        TsvEncodingHelper.forContentType("text/html");
    }

    @Test
    public void detectRawEncoding() {
        assertSame(RawValueEncoding.class, TsvEncodingHelper.forContentType("text/tab-separated-values").valueEncoding.getClass());
    }

    @Test
    public void detectUrlEncoding() {
        assertSame(UrlValueEncoding.class, TsvEncodingHelper.forContentType("text/tab-separated-values; colenc=U").valueEncoding.getClass());
    }

    @Test
    public void detectBase64Encoding() {
        assertSame(Base64ValueEncoding.class, TsvEncodingHelper.forContentType("text/tab-separated-values; colenc=B").valueEncoding.getClass());
    }

    @Test
    public void detectQuotedPrintableEncoding() {
        assertSame(QuotedPrintableValueEncoding.class, TsvEncodingHelper.forContentType("text/tab-separated-values; colenc=Q").valueEncoding.getClass());
    }
}
