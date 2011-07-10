package kyototycoon.tsv;

import kyototycoon.StopWatch;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TsvEncodingTest {
    private TsvEncoding dut;

    @Before
    public void beforeEach() {
        dut = new TsvEncoding(null, new RawValueEncoding());
    }

    @Test
    public void encode() {
        byte[] encoded = dut.encode(new Values().put("key".getBytes(), "value".getBytes()));
        assertThat(encoded, is("key\tvalue\r\n".getBytes()));
    }

    @Test public void decode() {
        Values decoded = dut.decode("key\tvalue\r\n".getBytes());
        assertThat(decoded, is(new Values().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void decode_a_line_ends_with_just_newline() {
        Values decoded = dut.decode("key\tvalue\n".getBytes());
        assertThat(decoded, is(new Values().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void decode_a_line_without_eol() {
        Values decoded = dut.decode("key\tvalue".getBytes());
        assertThat(decoded, is(new Values().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void encodingShouldBeFast() {
        byte[] key = "key".getBytes();
        byte[] value = "user:1234567890".getBytes();
        StopWatch watch = new StopWatch().start();
        for (int i = 0; i < 10000; i++) {
            Values values = new Values().put(key, value);
            dut.encode(values);
        }
        watch.stop();
        System.out.println("Elapsed: " + watch.elapsed());
        assertThat(watch.elapsed(), lessThan(300L));
    }
}
