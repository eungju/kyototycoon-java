package kyototycoon.benchmark;

import kyototycoon.Values;
import kyototycoon.networking.jetty.JettyNetworking;

import java.net.URI;

import static org.junit.Assert.*;

public class JettyNetworkingBenchmark {
	public static void main(String[] args) throws Exception {
		final JettyNetworking db = new JettyNetworking();
        db.initialize(new URI[] { URI.create("http://localhost:1978") });
        db.start();
        final Values input = new Values();
        input.put("key", "1234567890".getBytes());
        input.put("value", "1234567890".getBytes());
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
