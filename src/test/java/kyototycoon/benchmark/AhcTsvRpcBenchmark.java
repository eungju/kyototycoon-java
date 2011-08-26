package kyototycoon.benchmark;

import kyototycoon.ahc.AhcTsvRpcClient;

public class AhcTsvRpcBenchmark {
	public static void main(String[] args) throws Exception {
        new TsvRpcBenchmark(new AhcTsvRpcClient()).run();
	}
}
