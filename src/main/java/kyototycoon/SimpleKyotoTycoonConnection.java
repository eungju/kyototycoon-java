package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcConnection;

public class SimpleKyotoTycoonConnection extends SimpleKyotoTycoonRpc implements KyotoTycoonConnection {
    private final TsvRpcConnection connection;

    public SimpleKyotoTycoonConnection(TsvRpcConnection connection) {
        this.connection = connection;
        tsvRpc = connection;
    }

    public void close() {
        connection.close();
    }
}
