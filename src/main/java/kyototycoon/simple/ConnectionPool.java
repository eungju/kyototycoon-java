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

    public ConnectionPool(TsvRpcClient factory, int min, int max) {
        this.factory = factory;
        this.min = min;
        this.max = max;
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
            TsvRpcConnection connection = idle.poll(1000, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new RuntimeException();
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
