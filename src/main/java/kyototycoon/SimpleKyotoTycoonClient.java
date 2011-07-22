package kyototycoon;

import kyototycoon.finagle.FinagleTsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcClient;

import java.net.URI;

public class SimpleKyotoTycoonClient extends SimpleKyotoTycoonRpc implements KyotoTycoonClient {
    private final TsvRpcClient client;

    public SimpleKyotoTycoonClient(Iterable<URI> addresses) throws Exception {
        client = new FinagleTsvRpcClient();
        client.setHosts(addresses);
        client.start();
        tsvRpc = client;
    }

    public SimpleKyotoTycoonClient(TsvRpcClient client) {
        this.client = client;
        tsvRpc = client;
    }

    public void stop() {
        client.stop();
    }

    public KyotoTycoonConnection getConnection() {
        SimpleKyotoTycoonConnection connection = new SimpleKyotoTycoonConnection(client.getConnection());
        connection.setValueTranscoder(valueTranscoder);
        return connection;
    }
}
