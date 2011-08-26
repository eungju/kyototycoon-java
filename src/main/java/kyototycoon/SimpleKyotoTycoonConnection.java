package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SimpleKyotoTycoonConnection extends SimpleKyotoTycoonRpc implements KyotoTycoonConnection {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TsvRpcConnection connection;
    protected final List<Cursor> cursors;
    protected int cursorCount;

    public SimpleKyotoTycoonConnection(TsvRpcConnection connection) {
        this.connection = connection;
        tsvRpc = connection;
        cursors = new ArrayList<Cursor>();
        cursorCount = 0;
    }

    public void close() {
        for (Cursor cursor : cursors) {
            try {
                cursor.close();
            } catch (Exception e) {
                logger.warn("Error while closing a cursor", e);
            }
        }
        connection.close();
    }

    public Cursor cursor() {
        return new SimpleCursor(this, ++cursorCount);
    }
}
