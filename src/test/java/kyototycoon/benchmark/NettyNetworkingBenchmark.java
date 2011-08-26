package kyototycoon.benchmark;

import kyototycoon.netty.NettyTsvRpcClient;

public class NettyNetworkingBenchmark {
	public static void main(String[] args) throws Exception {
        new TsvRpcBenchmark(new NettyTsvRpcClient()).run();
	}
}
