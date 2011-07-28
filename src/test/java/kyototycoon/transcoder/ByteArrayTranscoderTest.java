package kyototycoon.transcoder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ByteArrayTranscoderTest {
    private ByteArrayTranscoder dut;

    @Before
    public void beforeEach() {
        dut = new ByteArrayTranscoder();
    }

    @Test
    public void encode() {
        byte[] value = new byte[] { 97 };
        assertThat(dut.encode(value), is(value));
    }

    @Test
    public void decode() {
        byte[] value = new byte[] { 97 };
        assertThat(dut.decode(value), is(value));
    }
}
