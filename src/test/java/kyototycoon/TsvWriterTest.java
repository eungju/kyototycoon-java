package kyototycoon;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TsvWriterTest {
    private TsvWriter dut;
    private ByteArrayOutputStream output;

    @Before
    public void beforeEach() {
        output = new ByteArrayOutputStream();
        dut = new TsvWriter(output);

    }

    @Test
    public void writeKey() throws IOException {
        dut.writeKey("key");
        assertThat(output.toByteArray(), is("key".getBytes()));
    }

    @Test
    public void writeTab() throws IOException {
        dut.writeTab();
        assertThat(output.toByteArray(), is("\t".getBytes()));
    }

    @Test
    public void writeValue() throws IOException {
        dut.writeKey("value");
        assertThat(output.toByteArray(), is("value".getBytes()));
    }


    @Test
    public void writeEol() throws IOException {
        dut.writeEol();
        assertThat(output.toByteArray(), is("\r\n".getBytes()));
    }
}
