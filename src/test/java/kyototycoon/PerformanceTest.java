package kyototycoon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PerformanceTest {
    RemoteDB dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new RemoteDB("localhost", 1978);
        dut.clear();
    }

    @After
    public void afterEach() {
        dut.destroy();
    }

	@Test
    public void singleThread() throws Exception {
        dut.set("key", "value");

        StopWatch watch = new StopWatch();
        final int n = 1000;
        watch.start();
        for (int i = 0; i < n; i++) {
		    dut.get("key");
        }
        watch.stop();
        System.out.println("Elapsed: " + watch.elapsed());
        double tps = n / ((double) watch.elapsed() / 1000);
        System.out.println("TPS: " + tps);
        assertThat(tps, greaterThan(300D));
	}
}
