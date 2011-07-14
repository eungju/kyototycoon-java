package kyototycoon.benchmark;

import kyototycoon.netty.NettyNetworking;
import kyototycoon.tsv.Values;

import java.net.URI;

import static org.junit.Assert.*;

public class NettyNetworkingBenchmark {
	public static void main(String[] args) throws Exception {
		final NettyNetworking db = new NettyNetworking();
        db.initialize(new URI[] { URI.create("http://localhost:1978") });
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
