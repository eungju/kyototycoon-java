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
    private ObjectLifecycle<TsvRpcConnection> connectionLifecycle;
    private ObjectPool<TsvRpcConnection> connectionPool;

    public TsvRpcConnection getConnection() {
        return connectionLifecycle.create();
    }

    public void setHost(URI address) {
        this.address = address;
    }

    public void setRequestTimeout(long timeout, TimeUnit unit) {
        this.timeout = (int) unit.toMillis(timeout);
    }

    public void start() {
        connectionLifecycle = new ConnectionLifecycle();
        connectionPool = new ObjectPool<TsvRpcConnection>(connectionLifecycle, 1, 10, timeout);
    }

    public void stop() {
        connectionPool.dispose();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        TsvRpcConnection connection = connectionPool.acquire();
        try {
            TsvRpcResponse response = connection.call(request);
            if (connection.isAlive()) {
                connectionPool.release(connection);
            } else {
                connectionPool.abandon(connection);
            }
            return response;
        } catch (Exception e) {
            connectionPool.abandon(connection);
            throw new RuntimeException(e);
        }
    }

    class ConnectionLifecycle implements ObjectLifecycle<TsvRpcConnection> {
        public TsvRpcConnection create() {
            try {
                return new SimpleTsvRpcConnection(address, timeout);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create a connection", e);
            }
        }

        public void destroy(TsvRpcConnection o) {
            o.close();
        }
    }
}
