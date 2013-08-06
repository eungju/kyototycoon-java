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
    private ObjectPool<TsvRpcConnection> pool;

    public TsvRpcConnection getConnection() {
        try {
            return new SimpleTsvRpcConnection(address, timeout);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create a connection", e);
        }
    }

    public void setHost(URI address) {
        this.address = address;
    }

    public void setRequestTimeout(long timeout, TimeUnit unit) {
        this.timeout = (int) unit.toMillis(timeout);
    }

    public void start() {
        pool = new ObjectPool<TsvRpcConnection>(new ObjectLifecycle<TsvRpcConnection>() {
            public TsvRpcConnection create() {
                return getConnection();
            }

            public void destroy(TsvRpcConnection o) {
                o.close();
            }
        }, 1, 10, timeout);
    }

    public void stop() {
        pool.dispose();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        TsvRpcConnection connection = pool.acquire();
        try {
            TsvRpcResponse response = connection.call(request);
            if (connection.isAlive()) {
                pool.release(connection);
            } else {
                pool.abandon(connection);
            }
            return response;
        } catch (Exception e) {
            pool.abandon(connection);
            throw new RuntimeException(e);
        }
    }
}
