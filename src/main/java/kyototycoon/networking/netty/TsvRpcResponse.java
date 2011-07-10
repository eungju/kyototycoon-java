package kyototycoon.networking.netty;

import kyototycoon.tsv.Values;

public class TsvRpcResponse {
    public final Values output;

    public TsvRpcResponse(Values output) {
        this.output = output;
    }
}
