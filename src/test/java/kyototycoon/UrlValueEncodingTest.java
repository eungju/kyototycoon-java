package kyototycoon;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UrlValueEncodingTest {
    private UrlValueEncoding dut;

    @Before
    public void beforeEach() {
        dut = new UrlValueEncoding();
    }

    @Test
    public void encode() throws IOException {
        assertThat(dut.encode("display name"), is("display+name"));
    }

    @Test public void decode() throws IOException {
        assertThat(dut.decode("display+name"), is("display name"));
    }
}
