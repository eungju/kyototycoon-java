package kyototycoon.tsvrpc;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class Base64ValueEncodingTest {
    private Base64ValueEncoding dut;

    @Before
    public void beforeEach() {
        dut = new Base64ValueEncoding();
    }

    @Test
    public void encode() throws IOException {
        assertThat(dut.encode("id".getBytes()), is("aWQ=".getBytes()));
    }

    @Test public void decode() throws IOException {
        assertThat(dut.decode("aWQ=".getBytes()), is("id".getBytes()));
    }
}
