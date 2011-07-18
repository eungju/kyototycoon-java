package kyototycoon;

import kyototycoon.finagle.FinagleTsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcClient;

import java.net.URI;

/**
 * TODO: Extract interface?
 * TODO: Lifecycle?
 */
public class KyotoTycoonClient extends KyotoTycoonRpc {
    private final TsvRpcClient client;

    public KyotoTycoonClient(Iterable<URI> addresses) throws Exception {
        client = new FinagleTsvRpcClient();
        client.setHosts(addresses);
        client.start();
        tsvRpc = client;
    }

    public KyotoTycoonClient(TsvRpcClient client) {
        this.client = client;
        tsvRpc = client;
    }

    public void close() {
        client.stop();
    }

    public KyotoTycoonConnection getConnection() {
        KyotoTycoonConnection connection = new KyotoTycoonConnection(client.getConnection());
        connection.setKeyTranscoder(keyTranscoder);
        connection.setValueTranscoder(valueTranscoder);
        return connection;
    }
}
