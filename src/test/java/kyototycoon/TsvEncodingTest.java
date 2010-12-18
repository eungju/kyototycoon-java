package kyototycoon;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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
        byte[] encoded = dut.encode(ImmutableMap.of("key", "value"));
        assertThat(encoded, is("key\tvalue\r\n".getBytes()));
    }

    @Test public void decode() {
        Map<String, String> decoded = dut.decode("key\tvalue\r\n".getBytes());
        assertThat(decoded, is((Map) ImmutableMap.of("key", "value")));
    }

    @Test public void decode_a_line_ends_with_just_newline() {
        Map<String, String> decoded = dut.decode("key\tvalue\n".getBytes());
        assertThat(decoded, is((Map) ImmutableMap.of("key", "value")));
    }

    @Test public void decode_a_line_without_eol() {
        Map<String, String> decoded = dut.decode("key\tvalue".getBytes());
        assertThat(decoded, is((Map) ImmutableMap.of("key", "value")));
    }
}
