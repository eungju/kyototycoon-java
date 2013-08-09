package kyototycoon.tsvrpc;

import kyototycoon.StopWatch;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
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
        ChannelBuffer encoded = dut.encode(new Assoc().put("key".getBytes(), "value".getBytes()));
        assertThat(encoded, is(ChannelBuffers.wrappedBuffer("key\tvalue\r\n".getBytes())));
    }

    @Test public void decode() {
        Assoc decoded = dut.decode(ChannelBuffers.wrappedBuffer("key\tvalue\r\n".getBytes()));
        assertThat(decoded, is(new Assoc().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void decode_a_line_ends_with_just_newline() {
        Assoc decoded = dut.decode(ChannelBuffers.wrappedBuffer("key\tvalue\n".getBytes()));
        assertThat(decoded, is(new Assoc().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void decode_a_line_without_eol() {
        Assoc decoded = dut.decode(ChannelBuffers.wrappedBuffer("key\tvalue".getBytes()));
        assertThat(decoded, is(new Assoc().put("key".getBytes(), "value".getBytes())));
    }

    @Test public void decode_a_line_which_does_not_have_a_value() {
        assertThat(dut.decode(ChannelBuffers.wrappedBuffer("key".getBytes())), is(new Assoc().put("key".getBytes(), new byte[0])));
        assertThat(dut.decode(ChannelBuffers.wrappedBuffer("key\r\n".getBytes())), is(new Assoc().put("key".getBytes(), new byte[0])));
        assertThat(dut.decode(ChannelBuffers.wrappedBuffer("key\t".getBytes())), is(new Assoc().put("key".getBytes(), new byte[0])));
        assertThat(dut.decode(ChannelBuffers.wrappedBuffer("key\t\r\n".getBytes())), is(new Assoc().put("key".getBytes(), new byte[0])));
    }

    @Test public void encodingShouldBeFast() {
        byte[] key = "key".getBytes();
        byte[] value = "user:1234567890".getBytes();
        Assoc assoc = new Assoc().put(key, value);
        StopWatch watch = new StopWatch().start();
        for (int i = 0; i < 10000; i++) {
            dut.encode(assoc);
        }
        watch.stop();
        System.out.println("Elapsed: " + watch.elapsed());
        assertThat(watch.elapsed(), lessThan(100L));
    }

    @Test public void decodingShouldBeFast() {
        byte[] key = "key".getBytes();
        byte[] value = "user:1234567890".getBytes();
        Assoc assoc = new Assoc().put(key, value);
        ChannelBuffer buffer = dut.encode(assoc);
        StopWatch watch = new StopWatch().start();
        for (int i = 0; i < 10000; i++) {
            dut.decode(buffer);
        }
        watch.stop();
        System.out.println("Elapsed: " + watch.elapsed());
        assertThat(watch.elapsed(), lessThan(100L));
    }
}
