package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;

import java.util.Map;

class SimpleCursor implements Cursor {
    private final SimpleKyotoTycoonConnection connection;
    private final long id;

    public SimpleCursor(SimpleKyotoTycoonConnection connection) {
        this.connection = connection;
        id = ++connection.cursorCount;
    }

    public void close() {
        Values input = new Values();
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_delete", input));
        if (response.status != 450) {
            connection.checkError(response);
        }
        connection.cursors.remove(this);
    }

    public void jump() {
        jump(null);
    }

    public void jump(Object key) {
        Values input = new Values();
        connection.setDbParameter(input);
        setCursorParameter(input);
        if (key != null) {
            input.put(SimpleKyotoTycoonRpc.Names.KEY, connection.keyTranscoder.encode(key));
        }
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_jump", input));
        connection.checkError(response);
    }

    public void jumpBack() {
        jumpBack(null);
    }

    public void jumpBack(Object key) {
        Values input = new Values();
        connection.setDbParameter(input);
        setCursorParameter(input);
        if (key != null) {
            input.put(SimpleKyotoTycoonRpc.Names.KEY, connection.keyTranscoder.encode(key));
        }
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_jump_back", input));
        connection.checkError(response);
    }

    public boolean step() {
        Values input = new Values();
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_step", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public boolean stepBack() {
        Values input = new Values();
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_step_back", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public Map.Entry<Object, Object> get(boolean step) {
        Values input = new Values();
        setCursorParameter(input);
        if (step) {
            input.put(SimpleKyotoTycoonRpc.Names.STEP, connection.encodeStr(String.valueOf(step)));
        }
        final TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get", input));
        if (response.status == 450) {
            //no record
            return null;
        }
        connection.checkError(response);
        return new Map.Entry<Object, Object>() {
            public Object getKey() {
                return connection.keyTranscoder.decode(response.output.get(SimpleKyotoTycoonRpc.Names.KEY));
            }

            public Object getValue() {
                return connection.valueTranscoder.decode(response.output.get(SimpleKyotoTycoonRpc.Names.VALUE));
            }

            public Object setValue(Object s) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Object getKey() {
        return getKey(false);
    }

    public Object getKey(boolean step) {
        Values input = new Values();
        setCursorParameter(input);
        if (step) {
            input.put(SimpleKyotoTycoonRpc.Names.STEP, connection.encodeStr(String.valueOf(step)));
        }
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get_key", input));
        connection.checkError(response);
        return connection.valueTranscoder.decode(response.output.get(SimpleKyotoTycoonRpc.Names.KEY));
    }

    void setCursorParameter(Values input) {
        input.put(SimpleKyotoTycoonRpc.Names.CUR, connection.encodeStr(String.valueOf(id)));
    }
}
