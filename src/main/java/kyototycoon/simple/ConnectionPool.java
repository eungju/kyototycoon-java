package kyototycoon.simple;

import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private final TsvRpcClient factory;
    private final BlockingQueue<TsvRpcConnection> idle;
    private final BlockingQueue<TsvRpcConnection> busy;
    private final int min;
    private final int max;
    private final int timeout;

    public ConnectionPool(TsvRpcClient factory, int min, int max, int timeout) {
        this.factory = factory;
        this.min = min;
        this.max = max;
        this.timeout = timeout;
        idle = new LinkedBlockingQueue<TsvRpcConnection>();
        busy = new LinkedBlockingQueue<TsvRpcConnection>();
        for (int i = 0; i < min; i++) {
            idle.add(factory.getConnection());
        }
    }

    public void dispose() {
        while (idle.isEmpty()) {
            idle.remove().close();
        }
    }

    public TsvRpcConnection acquire() {
        if (idle.isEmpty() && (idle.size() + busy.size()) < max) {
            idle.add(factory.getConnection());
        }
        try {
            TsvRpcConnection connection = idle.poll(timeout, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new RuntimeException("All connections are busy");
            }
            busy.add(connection);
            return connection;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void release(TsvRpcConnection connection) {
        busy.remove(connection);
        idle.add(connection);
    }

    public void abandon(TsvRpcConnection connection) {
        busy.remove(connection);
        connection.close();
    }
}
