package kyototycoon;

import kyototycoon.tsvrpc.Assoc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;

class SimpleCursor implements Cursor {
    private final SimpleKyotoTycoonConnection connection;
    private final long id;

    public SimpleCursor(SimpleKyotoTycoonConnection connection, int id) {
        this.connection = connection;
        this.id = ((long) id << 48) | ((System.currentTimeMillis() & 0xFFFF) << 32) | this.hashCode();
    }

    public void close() {
        Assoc input = new Assoc();
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_delete", input));
        if (response.status != 450) {
            connection.checkError(response);
        }
        connection.cursors.remove(this);
    }

    public boolean jump() {
        return jump(null);
    }

    public boolean jump(Object key) {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        connection.setDbParameter(input);
        setCursorParameter(input);
        if (key != null) {
            connection.setKeyParameter(input, key);
        }
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_jump", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public boolean jumpBack() {
        return jumpBack(null);
    }

    public boolean jumpBack(Object key) {
        Assoc input = new Assoc();
        connection.setDbParameter(input);
        connection.setSignalParameters(input);
        setCursorParameter(input);
        if (key != null) {
            connection.setKeyParameter(input, key);
        }
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_jump_back", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public boolean step() {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_step", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public boolean stepBack() {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_step_back", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public boolean setValue(Object value) {
        return setValue(value, ExpirationTime.NONE, false);
    }

    public boolean setValue(Object value, ExpirationTime xt, boolean step) {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        input.put(SimpleKyotoTycoonRpc.Names.VALUE, connection.valueTranscoder.encode(value));
        connection.setExpirationTimeParameter(input, xt);
        setStepParameter(input, step);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_set_value", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public boolean remove() {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_remove", input));
        if (response.status == 450) {
            return false;
        }
        connection.checkError(response);
        return true;
    }

    public Object getKey() {
        return getKey(false);
    }

    public Object getKey(boolean step) {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        setStepParameter(input, step);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get_key", input));
        if (response.status == 450) {
            return null;
        }
        connection.checkError(response);
        return connection.getKeyParameter(response.output);
    }


    public Object getValue() {
        return getValue(false);
    }

    public Object getValue(boolean step) {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        setStepParameter(input, step);
        TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get_value", input));
        if (response.status == 450) {
            return null;
        }
        connection.checkError(response);
        return connection.valueTranscoder.decode(response.output.get(SimpleKyotoTycoonRpc.Names.VALUE));
    }

    public Record get() {
        return get(false);
    }

    public Record get(boolean step) {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        setStepParameter(input, step);
        final TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_get", input));
        if (response.status == 450) {
            return null;
        }
        connection.checkError(response);
        return new Record(connection.getKeyParameter(response.output), connection.getValueParameter(response.output),
                connection.getExpirationTimeParameter(response.output));
    }

    public Record seize() {
        Assoc input = new Assoc();
        connection.setSignalParameters(input);
        setCursorParameter(input);
        final TsvRpcResponse response = connection.tsvRpc.call(new TsvRpcRequest("cur_seize", input));
        if (response.status == 450) {
            return null;
        }
        connection.checkError(response);
        return new Record(connection.getKeyParameter(response.output), connection.getValueParameter(response.output),
                connection.getExpirationTimeParameter(response.output));
    }

    void setCursorParameter(Assoc input) {
        input.put(SimpleKyotoTycoonRpc.Names.CUR, connection.encodeStr(String.valueOf(id)));
    }

    void setStepParameter(Assoc input, boolean step) {
        if (step) {
            input.put(SimpleKyotoTycoonRpc.Names.STEP, new byte[0]);
        }
    }
}
