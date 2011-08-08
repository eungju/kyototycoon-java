package kyototycoon;

import kyototycoon.transcoder.StringTranscoder;
import kyototycoon.transcoder.Transcoder;
import kyototycoon.tsvrpc.KeyValuePair;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public boolean cas(Object key, Object oval, Object nval) {
        return cas(key, oval, nval, ExpirationTime.NONE);
    }

    public boolean cas(Object key, Object oval, Object nval, ExpirationTime xt) {
        Values input = new Values().put(KEY, keyTranscoder.encode(key));
        if (oval != null) {
            input.put("oval".getBytes(), valueTranscoder.encode(oval));
        }
        if (nval != null) {
            input.put("nval".getBytes(), valueTranscoder.encode(nval));
        }
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("cas", input));
        if (response.status == 450) {
            return false;
        }
        checkError(response);
        return true;
    }

    public boolean remove(Object key) {
        Values input = new Values().put(KEY, keyTranscoder.encode(key));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("remove", input));
        if (response.status == 450) {
            return false;
        }
        checkError(response);
        return true;
    }

    public Object get(Object key) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("get", new Values().put(KEY, keyTranscoder.encode(key))));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return valueTranscoder.decode(response.output.get(VALUE));
    }

    public Object seize(Object key) {
        Values input = new Values().put(KEY, keyTranscoder.encode(key));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("seize", input));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return valueTranscoder.decode(response.output.get(VALUE));
    }

    public long setBulk(Map<Object, Object> entries) {
        return setBulk(entries, ExpirationTime.NONE, false);
    }

    public long setBulk(Map<Object, Object> entries, ExpirationTime xt, boolean atomic) {
        Values input = new Values();
        putExpirationTime(input, xt);
        if (atomic) {
            input.put("atomic".getBytes(), new byte[0]);
        }
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            byte[] key = keyTranscoder.encode(entry.getKey());
            byte[] value = valueTranscoder.encode(entry.getValue());
            byte[] markedKey = new byte[key.length + 1];
            markedKey[0] = '_';
            System.arraycopy(key, 0, markedKey, 1, key.length);
            input.put(markedKey, value);
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("set_bulk", input));
        checkError(response);
        return Long.parseLong(decodeStr(response.output.get(NUM)));
    }

    public long removeBulk(List<Object> keys) {
        return removeBulk(keys, false);
    }

    public long removeBulk(List<Object> keys, boolean atomic) {
        Values input = new Values();
        if (atomic) {
            input.put("atomic".getBytes(), new byte[0]);
        }
        for (Object key : keys) {
            byte[] bareKey = keyTranscoder.encode(key);
            byte[] markedKey = new byte[bareKey.length + 1];
            markedKey[0] = '_';
            System.arraycopy(bareKey, 0, markedKey, 1, bareKey.length);
            input.put(markedKey, new byte[0]);
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("remove_bulk", input));
        checkError(response);
        return Long.parseLong(decodeStr(response.output.get(NUM)));
    }

    public Map<Object, Object> getBulk(List<Object> keys) {
        return getBulk(keys, false);
    }

    public Map<Object, Object> getBulk(List<Object> keys, boolean atomic) {
        Values input = new Values();
        if (atomic) {
            input.put("atomic".getBytes(), new byte[0]);
        }
        for (Object key : keys) {
            byte[] bareKey = keyTranscoder.encode(key);
            byte[] markedKey = new byte[bareKey.length + 1];
            markedKey[0] = '_';
            System.arraycopy(bareKey, 0, markedKey, 1, bareKey.length);
            input.put(markedKey, new byte[0]);
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("get_bulk", input));
        checkError(response);
        Map<Object, Object> result = new HashMap<Object, Object>();
        for (KeyValuePair pair : response.output) {
            if (pair.key[0] == '_') {
                byte[] bareKey = new byte[pair.key.length - 1];
                System.arraycopy(pair.key, 1, bareKey, 0, bareKey.length);
                Object key = keyTranscoder.decode(bareKey);
                Object value = valueTranscoder.decode(pair.value);
                result.put(key, value);
            }
        }
        if (result.size() != Long.parseLong(decodeStr(response.output.get(NUM)))) {
            throw new AssertionError();
        }
        return result;
    }

    public void vacuum() {
        vacuum(0);
    }

    public void vacuum(long step) {
        Values input = new Values();
        if (step > 0) {
            input.put("step".getBytes(), encodeStr(String.valueOf(step)));
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("vacuum", input));
        checkError(response);
    }

    public List<Object> matchPrefix(Object prefix) {
        return matchPrefix(prefix, -1);
    }

    public List<Object> matchPrefix(Object prefix, long max) {
        Values input = new Values();
        input.put("prefix".getBytes(), keyTranscoder.encode(prefix));
        if (max >= 0) {
            input.put("max".getBytes(), encodeStr(String.valueOf(max)));
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("match_prefix", input));
        checkError(response);
        List<Object> result = new ArrayList<Object>();
        for (KeyValuePair pair : response.output) {
            if (pair.key[0] == '_') {
                byte[] bareKey = new byte[pair.key.length - 1];
                System.arraycopy(pair.key, 1, bareKey, 0, bareKey.length);
                Object key = keyTranscoder.decode(bareKey);
                result.add(key);
            }
        }
        if (result.size() != Long.parseLong(decodeStr(response.output.get(NUM)))) {
            throw new AssertionError();
        }
        return result;
    }

    public List<Object> matchRegex(Object regex) {
        return matchRegex(regex, -1);
    }

    public List<Object> matchRegex(Object prefix, long max) {
        Values input = new Values();
        input.put("regex".getBytes(), keyTranscoder.encode(prefix));
        if (max >= 0) {
            input.put("max".getBytes(), encodeStr(String.valueOf(max)));
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("match_regex", input));
        checkError(response);
        List<Object> result = new ArrayList<Object>();
        for (KeyValuePair pair : response.output) {
            if (pair.key[0] == '_') {
                byte[] bareKey = new byte[pair.key.length - 1];
                System.arraycopy(pair.key, 1, bareKey, 0, bareKey.length);
                Object key = keyTranscoder.decode(bareKey);
                result.add(key);
            }
        }
        if (result.size() != Long.parseLong(decodeStr(response.output.get(NUM)))) {
            throw new AssertionError();
        }
        return result;
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
