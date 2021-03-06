package kyototycoon;

import kyototycoon.simple.SimpleTsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Lifecycle?
 */
public class SimpleKyotoTycoonClient extends SimpleKyotoTycoonRpc implements KyotoTycoonClient {
    private final TsvRpcClient client;

    public SimpleKyotoTycoonClient() {
        this(new SimpleTsvRpcClient());
    }
    
    public SimpleKyotoTycoonClient(TsvRpcClient client) {
        this.client = client;
        tsvRpc = client;
    }

    public void setHost(URI address) {
        client.setHost(address);
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
        connection.setTarget(target);
        return connection;
    }
}
