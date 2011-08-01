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
    protected static final byte[] HARD = "hard".getBytes();
    protected static final byte[] COMMAND = "command".getBytes();
    protected static final byte[] KEY = "key".getBytes();
    protected static final byte[] VALUE = "value".getBytes();
    protected static final byte[] XT = "xt".getBytes();
    protected static final byte[] NUM = "num".getBytes();
    protected static final byte[] ORIG = "orig".getBytes();
    protected static final byte[] ERROR = "ERROR".getBytes();

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
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(decodeStr(pair.key), decodeStr(pair.value));
        }
        return result;
    }

    public Map<String,String> status() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("status", new Values()));
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(decodeStr(pair.key), decodeStr(pair.value));
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
        input.put(HARD, encodeStr(String.valueOf(hard)));
        input.put(COMMAND, encodeStr(command));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("synchronize", input));
        checkError(response);
    }

    public void set(Object key, Object value) {
        set(key, value, ExpirationTime.NONE);
    }
    
    public void set(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("set", input));
        checkError(response);
    }

    public void add(Object key, Object value) {
        add(key, value, ExpirationTime.NONE);
    }

    public void add(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("add", input));
        checkError(response);
    }

    public void replace(Object key, Object value) {
        replace(key, value, ExpirationTime.NONE);
    }

    public void replace(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("replace", input));
        checkError(response);
    }

    public void append(Object key, Object value) {
        append(key, value, ExpirationTime.NONE);
    }

    public void append(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("append", input));
        checkError(response);
    }

    public Object get(Object key) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("get", new Values().put(KEY, keyTranscoder.encode(key))));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return valueTranscoder.decode(response.output.get(VALUE));
    }

    public long increment(Object key, long num) {
    	return increment(key, num, IncrementOrigin.ZERO, ExpirationTime.NONE);
    }

    public long increment(Object key, long num, IncrementOrigin orig, ExpirationTime xt) {
        Values input = new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, encodeStr(String.valueOf(num)));
        if (orig == IncrementOrigin.SET) {
            input.put(ORIG, encodeStr(String.valueOf(Long.MAX_VALUE)));
        } else if (orig == IncrementOrigin.TRY) {
            input.put(ORIG, encodeStr(String.valueOf(Long.MIN_VALUE)));
        }
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment", input));
        checkError(response);
        return Long.parseLong(decodeStr(response.output.get(NUM)));
    }

    public double incrementDouble(Object key, double num) {
    	return incrementDouble(key, num, IncrementOrigin.ZERO, ExpirationTime.NONE);
    }

    public double incrementDouble(Object key, double num, IncrementOrigin orig, ExpirationTime xt) {
        Values input = new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, encodeStr(String.valueOf(num)));
        if (orig == IncrementOrigin.SET) {
            input.put(ORIG, encodeStr(String.valueOf(Double.POSITIVE_INFINITY)));
        } else if (orig == IncrementOrigin.TRY) {
            input.put(ORIG, encodeStr(String.valueOf(Double.NEGATIVE_INFINITY)));
        }
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment_double", input));
        checkError(response);
        return Double.parseDouble(decodeStr(response.output.get(NUM)));
    }

    //Utilities

    byte[] encodeStr(String s) {
        return s.getBytes();
    }

    String decodeStr(byte[] b) {
        return new String(b);
    }

    void putKeyAndValue(Values values, Object key, Object value) {
        values.put(KEY, keyTranscoder.encode(key)).put(VALUE, valueTranscoder.encode(value));
    }

    void putExpirationTime(Values values, ExpirationTime xt) {
        if (xt.isEnabled()) {
            values.put(XT, encodeStr(String.valueOf(xt.getValue())));
        }
    }

    void checkError(TsvRpcResponse response) {
        if (response.status == 200) {
            return;
        }
        byte[] error = response.output.get(ERROR);
        String message = (error == null || error.length == 0)
                ? "HTTP Status Code is " + response.status
                : decodeStr(error);
        throw new KyotoTycoonException(message);
    }
}
