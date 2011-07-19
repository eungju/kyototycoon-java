package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(JMock.class)
public class SimpleKyotoTycoonClientTest {
    Mockery mockery = new JUnit4Mockery();
    TsvRpcClient underlying;
    SimpleKyotoTycoonClient dut;

    @Before
    public void beforeEach() {
        underlying = mockery.mock(TsvRpcClient.class);
        dut = new SimpleKyotoTycoonClient(underlying);
    }

    @Test public void
    provideConnectionsWhichInheritTranscoders() {
        final TsvRpcConnection tsvRpcConn = mockery.mock(TsvRpcConnection.class);
        mockery.checking(new Expectations() {{
            oneOf(underlying).getConnection(); will(returnValue(tsvRpcConn));
        }});
        SimpleKyotoTycoonConnection conn = (SimpleKyotoTycoonConnection) dut.getConnection();
        assertThat(conn.keyTranscoder, is(dut.keyTranscoder));
        assertThat(conn.valueTranscoder, is(dut.valueTranscoder));
    }
}
