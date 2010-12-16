package kyototycoon;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PerformanceTest {
	@Test
    public void singleThread() throws Exception {
		RemoteDB dut = new RemoteDB("localhost", 1978);
        dut.set("key", "value");

        final int n = 1000;
        long startedAt = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
		    dut.get("key");
        }
        long elapsed = System.currentTimeMillis() - startedAt;
        assertThat(n / ((double) elapsed / 1000), greaterThan(300D));
	}
}
