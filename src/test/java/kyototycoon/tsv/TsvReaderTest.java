package kyototycoon.tsv;

import kyototycoon.tsv.TsvReader;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TsvReaderTest {
    @Test public void hasNothingToRead() {
        assertThat(new TsvReader("".getBytes()).hasRemaining(), is(false));
    }

    @Test public void hasSomethingToRead() {
        assertThat(new TsvReader("k".getBytes()).hasRemaining(), is(true));
    }

    @Test public void readKey() {
        assertThat(new TsvReader("key\t".getBytes()).readKey(), is("key".getBytes()));
    }

    @Test public void readTab() {
        TsvReader dut = new TsvReader("\t".getBytes());
        dut.readTab();
        assertThat(dut.hasRemaining(), is(false));
    }

    @Test public void readValue() {
        assertThat(new TsvReader("value\r\n".getBytes()).readValue(), is("value".getBytes()));
    }

    @Test public void readEol() {
        TsvReader dut = new TsvReader("\r\n".getBytes());
        dut.readEol();
        assertThat(dut.hasRemaining(), is(false));
    }
}
