package kyototycoon;

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
}
