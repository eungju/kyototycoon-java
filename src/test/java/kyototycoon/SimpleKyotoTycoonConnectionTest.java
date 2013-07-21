package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcConnection;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SimpleKyotoTycoonConnectionTest {
    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();
    SimpleKyotoTycoonConnection dut;
    TsvRpcConnection underlying;

    @Before public void beforeEach() {
        underlying = mockery.mock(TsvRpcConnection.class);
        dut = new SimpleKyotoTycoonConnection(underlying);
    }
    
    @Test public void
    closeUnderlyingConnection() {
        mockery.checking(new Expectations() {{
            oneOf(underlying).close();
        }});
        dut.close();
    }
}
