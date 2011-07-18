package kyototycoon.netty;

import kyototycoon.KyotoTycoonFixture;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.Values;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NettyTsvRpcClientIntegrationTest {
    private NettyTsvRpcClient dut;

    @Before
    public void beforeEach() {
        dut = new NettyTsvRpcClient();
        dut.setHosts(Arrays.asList(KyotoTycoonFixture.SERVER_ADDRESS));
        dut.start();
    }

    @Test
    public void call() throws Exception {
        dut.call(new TsvRpcRequest("void", new Values()));
    }

    @Test
    public void callTwice() throws Exception {
        dut.call(new TsvRpcRequest("void", new Values()));
        dut.call(new TsvRpcRequest("void", new Values()));
    }

    @Test
    public void callEcho() throws Exception {
        Values input = new Values();
        input.put("k1".getBytes(), "v1".getBytes());
        assertThat(dut.call(new TsvRpcRequest("echo", input)).output, is(input));
    }
}
