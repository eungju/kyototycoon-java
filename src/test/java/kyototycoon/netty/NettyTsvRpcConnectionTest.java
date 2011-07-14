package kyototycoon.netty;

import kyototycoon.netty.NettyTsvRpcConnection;
import kyototycoon.tsv.Values;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NettyTsvRpcConnectionTest {
    private NettyTsvRpcConnection dut;

    @Test
    public void call() {
        dut = new NettyTsvRpcConnection();
        dut.open(URI.create("http://localhost:1978"));
        Values input = new Values().put("key".getBytes(), "value".getBytes());
        assertThat(dut.call("echo", input), is(input));
        dut.close();
    }
}
