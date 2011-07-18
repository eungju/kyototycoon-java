package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcConnection;

/**
 * TODO: Extract interface?
 */
public class KyotoTycoonConnection extends KyotoTycoonRpc {
    private final TsvRpcConnection connection;

    public KyotoTycoonConnection(TsvRpcConnection connection) {
        this.connection = connection;
        tsvRpc = connection;
    }

    public void close() {
        connection.close();
    }
}
