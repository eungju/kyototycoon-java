package kyototycoon.benchmark;

import kyototycoon.KyotoTycoonFixture;
import kyototycoon.tsvrpc.Assoc;
import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcRequest;

import static org.junit.Assert.*;

public class TsvRpcBenchmark {
    private TsvRpcClient db;

    public TsvRpcBenchmark(TsvRpcClient db) {
        this.db = db;
    }

    public void run() {
        db.setHost(KyotoTycoonFixture.SERVER_ADDRESS);
        db.start();
        final Assoc input = new Assoc();
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
