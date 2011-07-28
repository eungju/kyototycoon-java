package kyototycoon.benchmark;

import kyototycoon.KyotoTycoonFixture;
import kyototycoon.finagle.FinagleTsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;

import java.util.Arrays;

import static org.junit.Assert.*;

public class FinagleTsvRpcBenchmark {
	public static void main(String[] args) throws Exception {
		final FinagleTsvRpcClient db = new FinagleTsvRpcClient();
        db.setHosts(Arrays.asList(KyotoTycoonFixture.SERVER_ADDRESS));
        db.start();
        final Values input = new Values();
        input.put("key".getBytes(), "1234567890".getBytes());
        input.put("value".getBytes(), "1234567890".getBytes());
        final TsvRpcRequest request = new TsvRpcRequest("echo", input);
        final TsvRpcResponse response = new TsvRpcResponse(200, request.input);
		Runnable task = new Runnable() {
			public void run() {
                assertEquals(response.output, db.call(request).output);
			}
		};
		for (int c : new int[] { 1, 10, 100, 200, 300 }) {
			System.out.println(c + ":" + (new Benchmark(c, 10000).run(task)) + "ms");
		}
		db.stop();
	}
}
