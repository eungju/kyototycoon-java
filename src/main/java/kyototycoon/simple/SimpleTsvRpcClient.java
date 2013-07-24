package kyototycoon.simple;

import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class SimpleTsvRpcClient implements TsvRpcClient {
    private URI address;
    private int timeout = 1000;
    private TsvRpcConnection connection;

    public TsvRpcConnection getConnection() {
        try {
            return new SimpleTsvRpcConnection(address, timeout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setHost(URI address) {
        this.address = address;
    }

    public void setRequestTimeout(long timeout, TimeUnit unit) {
        this.timeout = (int) unit.toMillis(timeout);
    }

    public void start() {
        connection = getConnection();
    }

    public void stop() {
        connection.close();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        synchronized (this) {
            return connection.call(request);
        }
    }
}
