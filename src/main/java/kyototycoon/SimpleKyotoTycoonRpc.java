package kyototycoon;

import kyototycoon.transcoder.StringTranscoder;
import kyototycoon.transcoder.Transcoder;
import kyototycoon.tsvrpc.KeyValuePair;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;

import java.util.HashMap;
import java.util.Map;

public abstract class SimpleKyotoTycoonRpc implements KyotoTycoonRpc {
    protected static final byte[] KEY = "key".getBytes();
    protected static final byte[] VALUE = "value".getBytes();
    protected static final byte[] XT = "xt".getBytes();
    protected static final byte[] NUM = "num".getBytes();
    protected static final byte[] ERROR = "ERROR".getBytes();
    
    protected static final int STATUS_OK = 200;

    protected final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    protected final Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    protected Transcoder valueTranscoder = StringTranscoder.INSTANCE;
    protected TsvRpc tsvRpc;

    public void setValueTranscoder(Transcoder transcoder) {
        valueTranscoder = transcoder;
    }

    public Map<String,String> report() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("report", new Values()));
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(stringTranscoder.decode(pair.key), stringTranscoder.decode(pair.value));
        }
        return result;
    }

    public Map<String,String> status() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("status", new Values()));
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(stringTranscoder.decode(pair.key), stringTranscoder.decode(pair.value));
        }
        return result;
    }

    public void clear() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("clear", new Values()));
        checkError(response);
    }

    public void synchronize(boolean hard) {
        synchronize(hard, "");
    }

    public void synchronize(boolean hard, String command) {
        Values input = new Values();
        input.put("hard".getBytes(), stringTranscoder.encode(String.valueOf(hard)));
        input.put("command".getBytes(), stringTranscoder.encode(command));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("synchronize", input));
        checkError(response);
    }

    public void set(String key, Object value) {
        set(key, value, Long.MAX_VALUE);
    }
    
    public void set(String key, Object value, long xt) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("set", new Values()
                .put(KEY, keyTranscoder.encode(key))
                .put(VALUE, valueTranscoder.encode(value))
                .put(XT, valueTranscoder.encode(String.valueOf(xt)))));
        checkError(response);
    }

    public void add(String key, Object value) {
        add(key, value, Long.MAX_VALUE);
    }

    public void add(String key, Object value, long xt) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("add", new Values()
                .put(KEY, keyTranscoder.encode(key))
                .put(VALUE, valueTranscoder.encode(value))
                .put(XT, valueTranscoder.encode(String.valueOf(xt)))));
        checkError(response);
    }

    public void replace(String key, Object value) {
        replace(key, value, Long.MAX_VALUE);
    }

    public void replace(String key, Object value, long xt) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("replace", new Values()
                .put(KEY, keyTranscoder.encode(key))
                .put(VALUE, valueTranscoder.encode(value))
                .put(XT, valueTranscoder.encode(String.valueOf(xt)))));
        checkError(response);
    }

    public void append(String key, Object value) {
        append(key, value, Long.MAX_VALUE);
    }

    public void append(String key, Object value, long xt) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("append", new Values()
                .put(KEY, keyTranscoder.encode(key))
                .put(VALUE, valueTranscoder.encode(value))
                .put(XT, valueTranscoder.encode(String.valueOf(xt)))));
        checkError(response);
    }

    //TODO:
    public Object get(String key) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("get", new Values().put(KEY, keyTranscoder.encode(key))));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return valueTranscoder.decode(response.output.get(VALUE));
    }

    public long increment(String key, long num) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num)))));
        checkError(response);
        return Long.parseLong(stringTranscoder.decode(response.output.get(NUM)));
    }

    public double incrementDouble(String key, double num) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment_double", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num)))));
        checkError(response);
        return Double.parseDouble(stringTranscoder.decode(response.output.get(NUM)));
    }

    protected void checkError(TsvRpcResponse response) {
        if (response.status == 200) {
            return;
        }
        byte[] error = response.output.get(ERROR);
        String message = (error == null || error.length == 0)
                ? "HTTP Status Code is " + response.status
                : stringTranscoder.decode(error);
        throw new RuntimeException(message);
    }
}
