package kyototycoon.benchmark;

import kyototycoon.finagle.FinagleTsvRpcClient;

public class FinagleTsvRpcBenchmark {
	public static void main(String[] args) throws Exception {
		new TsvRpcBenchmark(new FinagleTsvRpcClient()).run();
	}
}
