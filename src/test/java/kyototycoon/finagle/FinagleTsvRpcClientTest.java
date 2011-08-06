package kyototycoon.finagle;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FinagleTsvRpcClientTest {
    private FinagleTsvRpcClient dut;

    @Before
    public void beforeEach() {
        dut = new FinagleTsvRpcClient();
    }

    @Test public void get_components() {
        List<URI> actual = dut.getComponents(URI.create("lb:(http://localhost:1978,http://localhost:1979)"));
        assertThat(actual, hasItems(URI.create("http://localhost:1978"), URI.create("http://localhost:1979")));
    }
}
package kyototycoon.finagle;

import org.junit.Before;

public class FinagleTsvRpcClientTest {
    private FinagleTsvRpcClient dut;

    @Before
    public void beforeEach() {
        dut = new FinagleTsvRpcClient();
    }
//
//    @Test public void get_components() {
//        List<URI> actual = dut.getComponents(URI.create("lb:(http://localhost:1978,http://localhost:1979)"));
//        assertThat(actual, hasItems(URI.create("http://localhost:1978"), URI.create("http://localhost:1979")));
//    }
}
