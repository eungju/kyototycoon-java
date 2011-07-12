package kyototycoon.benchmark;

import kyototycoon.FinagleKyotoTycoonClient;
import kyototycoon.tsv.Values;

import java.net.URI;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FinagleBenchmark {
	public static void main(String[] args) throws Exception {
		final FinagleKyotoTycoonClient db = new FinagleKyotoTycoonClient();
        db.setHosts(Arrays.asList(URI.create("http://localhost:1978")));
        db.start();
        final Values input = new Values();
        input.put("key".getBytes(), "1234567890".getBytes());
        input.put("value".getBytes(), "1234567890".getBytes());
		Runnable task = new Runnable() {
			public void run() {
                assertEquals(input, db.call("echo", input));
			}
		};
		for (int c : new int[] { 1, 10, 100, 200, 300 }) {
			System.out.println(c + ":" + (new Benchmark(c, 10000).run(task)) + "ms");
		}
		db.stop();
	}
}
