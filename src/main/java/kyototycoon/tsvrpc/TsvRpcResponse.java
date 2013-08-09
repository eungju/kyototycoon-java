package kyototycoon.tsvrpc;

public class TsvRpcResponse {
    public final int status;
    public final Assoc output;

    public TsvRpcResponse(int status, Assoc output) {
        this.status = status;
        this.output = output;
    }
    
    @Override
    public String toString() {
    	return status + " " + output.toString();
    }
}
