package kyototycoon.benchmark;

import kyototycoon.simple.SimpleTsvRpcClient;

public class SimpleTsvRpcBenchmark {
	public static void main(String[] args) throws Exception {
		new TsvRpcBenchmark(new SimpleTsvRpcClient()).run();
	}
}
