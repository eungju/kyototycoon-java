package kyototycoon.tsvrpc;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TsvEncodingHelperTest {
    @Test(expected=IllegalArgumentException.class)
    public void detectUnknownEncoding() {
        TsvEncodingHelper.forContentType("text/html");
    }

    @Test
    public void detectRawEncoding() {
        assertThat(TsvEncodingHelper.forContentType("text/tab-separated-values").valueEncoding, is(RawValueEncoding.class));
    }

    @Test
    public void detectUrlEncoding() {
        assertThat(TsvEncodingHelper.forContentType("text/tab-separated-values; colenc=U").valueEncoding, is(UrlValueEncoding.class));
    }

    @Test
    public void detectBase64Encoding() {
        assertThat(TsvEncodingHelper.forContentType("text/tab-separated-values; colenc=B").valueEncoding, is(Base64ValueEncoding.class));
    }

    @Test
    public void detectQuotedPrintableEncoding() {
        assertThat(TsvEncodingHelper.forContentType("text/tab-separated-values; colenc=Q").valueEncoding, is(QuotedPrintableValueEncoding.class));
    }
}
