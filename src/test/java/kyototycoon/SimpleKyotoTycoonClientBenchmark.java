package kyototycoon;

import kyototycoon.benchmark.Benchmark;

import static org.junit.Assert.*;

public class SimpleKyotoTycoonClientBenchmark {
	public static void main(String[] args) throws Exception {
		final SimpleKyotoTycoonClient db = new SimpleKyotoTycoonClient();
        db.setHost(KyotoTycoonFixture.SERVER_ADDRESS);
        db.start();
        final String key = "1234567890";
        final String value = "abcdefghijklmnopqrstuvwxyz";
        db.set(key, value);

		Runnable task = new Runnable() {
			public void run() {
                assertEquals(value, db.get(key));
			}
		};
		for (int c : new int[] { 1, 10, 100, 200, 300 }) {
			System.out.println(c + ":" + (new Benchmark(c, 10000).run(task)) + "ms");
		}
		db.stop();
	}
}
