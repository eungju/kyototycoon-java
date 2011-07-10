package kyototycoon.networking.netty;

import kyototycoon.Values;

public class TsvRpcRequest {
    public final String procedure;
    public final Values input;

    public TsvRpcRequest(String procedure, Values input) {
        this.procedure = procedure;
        this.input = input;
    }
}
