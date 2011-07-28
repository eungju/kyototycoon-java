package kyototycoon.tsvrpc;

public class TsvRpcResponse {
    public final int status;
    public final Values output;

    public TsvRpcResponse(int status, Values output) {
        this.status = status;
        this.output = output;
    }
    
    @Override
    public String toString() {
    	return status + " " + output.toString();
    }
}
