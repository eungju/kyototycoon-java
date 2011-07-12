package kyototycoon.tsv;

public class TsvRpcResponse {
    public final int status;
    public final Values output;

    public TsvRpcResponse(int status, Values output) {
        this.status = status;
        this.output = output;
    }
}
