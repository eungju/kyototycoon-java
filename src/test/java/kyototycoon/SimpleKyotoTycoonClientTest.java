package kyototycoon;

import kyototycoon.transcoder.Transcoder;
import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SimpleKyotoTycoonClientTest {
    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();
    TsvRpcClient underlying;
    SimpleKyotoTycoonClient dut;

    @Before
    public void beforeEach() {
        underlying = mockery.mock(TsvRpcClient.class);
        dut = new SimpleKyotoTycoonClient(underlying);
    }

    @Test public void
    connections_inherit_common_parameters() {
        final TsvRpcConnection tsvRpcConn = mockery.mock(TsvRpcConnection.class);
        mockery.checking(new Expectations() {{
            oneOf(underlying).getConnection(); will(returnValue(tsvRpcConn));
        }});
        dut.setKeyTranscoder(mockery.mock(Transcoder.class, "keyTranscoder"));
        dut.setValueTranscoder(mockery.mock(Transcoder.class, "valueTranscoder"));
        dut.setTarget("*");
        SimpleKyotoTycoonConnection conn = (SimpleKyotoTycoonConnection) dut.getConnection();
        assertThat(conn.keyTranscoder, is(dut.keyTranscoder));
        assertThat(conn.valueTranscoder, is(dut.valueTranscoder));
        assertThat(conn.target, is(dut.target));
    }
}
