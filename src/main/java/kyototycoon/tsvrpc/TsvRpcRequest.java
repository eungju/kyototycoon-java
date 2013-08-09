package kyototycoon.tsvrpc;

public class TsvRpcRequest {
    public final String procedure;
    public final Assoc input;

    public TsvRpcRequest(String procedure, Assoc input) {
        this.procedure = procedure;
        this.input = input;
    }
    
    @Override
    public String toString() {
    	return procedure + " " + input.toString();
    }
}
