package kyototycoon;

import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;

class SimpleCursor implements Cursor {
    private final SimpleKyotoTycoonConnection connection;
    private final long id;

    public SimpleCursor(SimpleKyotoTycoonConnection connection, int id) {
        this.connection = connection;
        this.id = ((long) id << 48) | ((System.currentTimeMillis() & 0xFFFF) << 32) | this.hashCode();
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
            connection.setKeyParameter(input, key);
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
            connection.setKeyParameter(input, key);
        }
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_jump_back", input));
        connection.checkError(response);
    }

    public boolean step() {
        Values input = new Values();
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_step", input));
        if (response.status == 450) {
            //no record
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
            //no record
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public void setValue(Object value) {
        setValue(value, ExpirationTime.NONE, false);
    }

    public void setValue(Object value, ExpirationTime xt, boolean step) {
        Values input = new Values();
        setCursorParameter(input);
        input.put(SimpleKyotoTycoonRpc.Names.VALUE, connection.valueTranscoder.encode(value));
        connection.setExpirationTimeParameter(input, xt);
        setStepParameter(input, step);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_set_value", input));
        connection.checkError(response);
    }

    public boolean remove() {
        Values input = new Values();
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_remove", input));
        if (response.status == 450) {
            //no record
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public Object getKey() {
        return getKey(false);
    }

    public Object getKey(boolean step) {
        Values input = new Values();
        setCursorParameter(input);
        setStepParameter(input, step);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get_key", input));
        if (response.status == 450) {
            //no record
            return null;
        }
        connection.checkError(response);
        return connection.getKeyParameter(response.output);
    }


    public Object getValue() {
        return getValue(false);
    }

    public Object getValue(boolean step) {
        Values input = new Values();
        setCursorParameter(input);
        setStepParameter(input, step);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get_value", input));
        if (response.status == 450) {
            //no record
            return null;
        }
        connection.checkError(response);
        return connection.valueTranscoder.decode(response.output.get(SimpleKyotoTycoonRpc.Names.VALUE));
    }

    public Record get() {
        return get(false);
    }

    public Record get(boolean step) {
        Values input = new Values();
        setCursorParameter(input);
        setStepParameter(input, step);
        final TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get", input));
        if (response.status == 450) {
            //no record
            return null;
        }
        connection.checkError(response);
        return new Record(connection.getKeyParameter(response.output), connection.getValueParameter(response.output),
                connection.getExpirationTimeParameter(response.output));
    }

    public Record seize() {
        Values input = new Values();
        setCursorParameter(input);
        final TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_seize", input));
        if (response.status == 450) {
            //no record
            return null;
        }
        connection.checkError(response);
        return new Record(connection.getKeyParameter(response.output), connection.getValueParameter(response.output),
                connection.getExpirationTimeParameter(response.output));
    }

    void setCursorParameter(Values input) {
        input.put(SimpleKyotoTycoonRpc.Names.CUR, connection.encodeStr(String.valueOf(id)));
    }

    void setStepParameter(Values input, boolean step) {
        if (step) {
            input.put(SimpleKyotoTycoonRpc.Names.STEP, new byte[0]);
        }
    }
}
