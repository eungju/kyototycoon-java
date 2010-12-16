package kyototycoon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class RemoteDBTest {
    private RemoteDB dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new RemoteDB("localhost", 1978);
    }

    @After
    public void afterEach() {
        dut.destroy();
    }

    @Test public void setAndGet() {
        dut.set("key", "value");
        assertThat(dut.get("key"), is("value"));
    }
}
