package kyototycoon.tsv;

public interface TsvRpcClient {
    byte[] KEY = "key".getBytes();
    byte[] VALUE = "value".getBytes();
    byte[] NUM = "num".getBytes();
    byte[] ERROR = "ERROR".getBytes();

    Values call(String procedure, Values input);
}
