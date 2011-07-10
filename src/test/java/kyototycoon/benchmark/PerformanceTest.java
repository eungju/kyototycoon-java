package kyototycoon.benchmark;

import kyototycoon.RemoteDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PerformanceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    RemoteDB dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new RemoteDB(new URI[] { URI.create("http://localhost:1978") });
        dut.clear();
    }

    @After
    public void afterEach() {
        dut.close();
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
        logger.info("Elapsed: {}", watch.elapsed());
        double tps = n / ((double) watch.elapsed() / 1000);
        logger.info("TPS: {}", tps);
        assertThat(tps, greaterThan(300D));
	}

    @Test
    public void incrementsOnSameKey() throws Exception {
        StopWatch watch = new StopWatch();
        final int n = 1000;
        watch.start();
        for (int i = 0; i < n; i++) {
            dut.increment("hit", 1);
        }
        watch.stop();
        logger.info("Elapsed: {}", watch.elapsed());
        double tps = n / ((double) watch.elapsed() / 1000);
        logger.info("TPS: {}", tps);
        assertThat(tps, greaterThan(300D));
    }
}
