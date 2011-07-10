package kyototycoon.networking.netty;

import kyototycoon.Values;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NettyNetworkingTest {
    private NettyNetworking dut;

    @Before
    public void beforeEach() {
        dut = new NettyNetworking();
        dut.initialize(new URI[] { URI.create("http://localhost:1978") });
        dut.start();
    }

    @Test
    public void call() throws Exception {
        dut.call("void", new Values());
    }

    @Test
    public void callTwice() throws Exception {
        dut.call("void", new Values());
        dut.call("void", new Values());
    }

    @Test
    public void callEcho() throws Exception {
        Values input = new Values();
        input.put("k1", "v1".getBytes());
        assertThat(dut.call("echo", input), is(input));
    }
}
