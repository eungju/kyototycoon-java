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
    protected static final byte[] NUM = "num".getBytes();
    protected static final byte[] ERROR = "ERROR".getBytes();
    protected final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    protected Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    protected Transcoder valueTranscoder = StringTranscoder.INSTANCE;
    protected TsvRpc tsvRpc;

    public void setKeyTranscoder(Transcoder transcoder) {
        keyTranscoder = transcoder;
    }

    public void setValueTranscoder(Transcoder transcoder) {
        valueTranscoder = transcoder;
    }

    public Map<String,String> report() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("report", new Values()));
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(stringTranscoder.decode(pair.key), stringTranscoder.decode(pair.value));
        }
        return result;
    }

    public Map<String,String> status() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("status", new Values()));
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(stringTranscoder.decode(pair.key), stringTranscoder.decode(pair.value));
        }
        return result;
    }

    public void set(Object key, Object value) {
        tsvRpc.call(new TsvRpcRequest("set", new Values().put(KEY, keyTranscoder.encode(key)).put(VALUE, valueTranscoder.encode(value))));
    }

    public Object get(Object key) {
        Values output = tsvRpc.call(new TsvRpcRequest("get", new Values().put(KEY, keyTranscoder.encode(key)))).output;
        byte[] value = output.get(VALUE);
        return value == null ? null : valueTranscoder.decode(value);
    }

    public void clear() {
        tsvRpc.call(new TsvRpcRequest("clear", new Values()));
    }

    public long increment(Object key, long num) {
        Values output = tsvRpc.call(new TsvRpcRequest("increment", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num))))).output;
        checkError(output);
        return Long.parseLong(stringTranscoder.decode(output.get(NUM)));
    }

    public double incrementDouble(Object key, double num) {
        Values output = tsvRpc.call(new TsvRpcRequest("increment_double", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num))))).output;
        checkError(output);
        return Double.parseDouble(stringTranscoder.decode(output.get(NUM)));
    }

    void checkError(Values output) {
        byte[] error = output.get(ERROR);
        if (error != null) {
            throw new RuntimeException(stringTranscoder.decode(error));
        }
    }
}
