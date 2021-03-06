package kyototycoon.tsvrpc;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class QuotedPrintableValueEncodingTest {
    private QuotedPrintableValueEncoding dut;

    @Before
    public void beforeEach() {
        dut = new QuotedPrintableValueEncoding();
    }

    @Test
    public void encode() throws IOException {
        assertThat(dut.encode("truth=beauty".getBytes()), is("truth=3Dbeauty".getBytes()));
    }

    @Test public void decode() throws IOException {
        assertThat(dut.decode("truth=3Dbeauty".getBytes()), is("truth=beauty".getBytes()));
    }
}
