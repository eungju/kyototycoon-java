package kyototycoon.benchmark;

import kyototycoon.KyotoTycoonFixture;
import kyototycoon.ahc.AhcTsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.Values;

import static org.junit.Assert.*;

public class AhcTsvRpcBenchmark {
	public static void main(String[] args) throws Exception {
		final AhcTsvRpcClient db = new AhcTsvRpcClient();
        db.setHost(KyotoTycoonFixture.SERVER_ADDRESS);
        db.start();
        final Values input = new Values();
        input.put("key".getBytes(), "1234567890".getBytes());
        input.put("value".getBytes(), "1234567890".getBytes());
        final TsvRpcRequest request = new TsvRpcRequest("echo", input);
		Runnable task = new Runnable() {
			public void run() {
                assertEquals(input, db.call(request).output);
			}
		};
		for (int c : new int[] { 1, 10, 100, 200, 300 }) {
			System.out.println(c + ":" + (new Benchmark(c, 10000).run(task)) + "ms");
		}
		db.stop();
	}
}
