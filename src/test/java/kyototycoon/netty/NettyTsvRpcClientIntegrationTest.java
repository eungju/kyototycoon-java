package kyototycoon.netty;

import kyototycoon.KyotoTycoonFixture;
import kyototycoon.tsvrpc.Assoc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NettyTsvRpcClientIntegrationTest {
    private NettyTsvRpcClient dut;

    @Before
    public void beforeEach() {
        dut = new NettyTsvRpcClient();
        dut.setHost(KyotoTycoonFixture.SERVER_ADDRESS);
        dut.start();
    }

    @Test
    public void call() throws Exception {
        dut.call(new TsvRpcRequest("void", new Assoc()));
    }

    @Test
    public void callTwice() throws Exception {
        dut.call(new TsvRpcRequest("void", new Assoc()));
        dut.call(new TsvRpcRequest("void", new Assoc()));
    }

    @Test
    public void callEcho() throws Exception {
        Assoc input = new Assoc();
        input.put("k1".getBytes(), "v1".getBytes());
        assertThat(dut.call(new TsvRpcRequest("echo", input)).output, is(input));
    }
}
