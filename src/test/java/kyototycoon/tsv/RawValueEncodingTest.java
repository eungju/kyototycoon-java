package kyototycoon.tsv;

import kyototycoon.tsv.RawValueEncoding;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class RawValueEncodingTest {
    private RawValueEncoding dut;

    @Before
    public void beforeEach() {
        dut = new RawValueEncoding();
    }

    @Test
    public void encode() {
        assertThat(dut.encode("v".getBytes()), is("v".getBytes()));
    }

    @Test public void decode() {
        assertThat(dut.encode("v".getBytes()), is("v".getBytes()));
    }
}
