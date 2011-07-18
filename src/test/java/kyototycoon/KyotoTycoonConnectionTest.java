package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcConnection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

public class KyotoTycoonConnectionTest {
    Mockery mockery = new JUnit4Mockery();
    KyotoTycoonConnection dut;
    TsvRpcConnection underlying;

    @Before public void beforeEach() {
        underlying = mockery.mock(TsvRpcConnection.class);
        dut = new KyotoTycoonConnection(underlying);
    }
    
    @Test public void
    closeUnderlyingConnection() {
        mockery.checking(new Expectations() {{
            oneOf(underlying).close();
        }});
        dut.close();
    }
}
