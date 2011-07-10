package kyototycoon.transcoder;

import kyototycoon.transcoder.StringTranscoder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class StringTranscoderTest {
    private StringTranscoder dut;

    @Before
    public void beforeEach() {
        dut = new StringTranscoder();
    }

    @Test
    public void encode() {
        assertThat(dut.encode("a"), is(new byte[]{ 97 }));
    }

    @Test
    public void decode() {
        assertThat(dut.decode(new byte[] { 97 }), is((Object) "a"));
    }
}
