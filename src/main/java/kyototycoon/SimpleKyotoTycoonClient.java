package kyototycoon;

import kyototycoon.finagle.FinagleTsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Lifecycle?
 */
public class SimpleKyotoTycoonClient extends SimpleKyotoTycoonRpc implements KyotoTycoonClient {
    private final TsvRpcClient client;

    public SimpleKyotoTycoonClient() {
        this(new FinagleTsvRpcClient());
    }
    
    public SimpleKyotoTycoonClient(TsvRpcClient client) {
        this.client = client;
        tsvRpc = client;
    }

    public void setHosts(Iterable<URI> addresses) {
        client.setHosts(addresses);
    }

    public void setRequestTimeout(long timeout, TimeUnit unit) {
        client.setRequestTimeout(timeout, unit);
    }

    public void start() {
        client.start();
    }
    
    public void stop() {
        client.stop();
    }

    public KyotoTycoonConnection getConnection() {
        SimpleKyotoTycoonConnection connection = new SimpleKyotoTycoonConnection(client.getConnection());
        connection.setKeyTranscoder(keyTranscoder);
        connection.setValueTranscoder(valueTranscoder);
        return connection;
    }
}
