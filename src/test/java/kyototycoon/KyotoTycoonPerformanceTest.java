package kyototycoon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class KyotoTycoonPerformanceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    SimpleKyotoTycoonClient dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new SimpleKyotoTycoonClient(Arrays.asList(KyotoTycoonFixture.SERVER_ADDRESS));
        dut.clear();
    }

    @After
    public void afterEach() {
        dut.stop();
    }

	@Test
    public void singleThread() throws Exception {
        dut.set("key", "value");

        StopWatch watch = new StopWatch();
        final int n = 1000;
        watch.start();
        for (int i = 0; i < n; i++) {
		    assertThat((String) dut.get("key"), is("value"));
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
            assertThat(dut.increment("hit", 1), is(i + 1L));
        }
        watch.stop();
        logger.info("Elapsed: {}", watch.elapsed());
        double tps = n / ((double) watch.elapsed() / 1000);
        logger.info("TPS: {}", tps);
        assertThat(tps, greaterThan(300D));
    }
}
