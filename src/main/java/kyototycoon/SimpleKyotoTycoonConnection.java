package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleKyotoTycoonConnection extends SimpleKyotoTycoonRpc implements KyotoTycoonConnection {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TsvRpcConnection connection;
    private final List<Cursor> cursors;
    private int cursorCount;

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
                logger.warn("Error while closing cursor", e);
            }
        }
        connection.close();
    }

    public Cursor cursor() {
        return new Cursor() {
            private final long id = ++cursorCount;

            public void close() {
                Values input = new Values();
                setCursorParameter(input);
                TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("cur_delete", input));
                checkError(response);
                cursors.remove(this);
            }

            public void jump() {
                jump(null);
            }

            public void jump(Object key) {
                Values input = createInputWithTarget();
                setCursorParameter(input);
                if (key != null) {
                    input.put(Names.KEY, keyTranscoder.encode(key));
                }
                TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("cur_jump", input));
                checkError(response);
            }

            public Map.Entry<Object, Object> get(boolean step) {
                Values input = new Values();
                setCursorParameter(input);
                if (step) {
                    input.put(Names.STEP, encodeStr(String.valueOf(step)));
                }
                final TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("cur_get", input));
                if (response.status == 450) {
                    //no record
                    return null;
                }
                checkError(response);
                return new Map.Entry<Object, Object>() {
                    public Object getKey() {
                        return keyTranscoder.decode(response.output.get(Names.KEY));
                    }

                    public Object getValue() {
                        return valueTranscoder.decode(response.output.get(Names.VALUE));
                    }

                    public Object setValue(Object s) {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            void setCursorParameter(Values input) {
                input.put(Names.CUR, encodeStr(String.valueOf(id)));
            }
        };
    }
}
