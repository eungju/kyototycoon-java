package kyototycoon.transcoder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
