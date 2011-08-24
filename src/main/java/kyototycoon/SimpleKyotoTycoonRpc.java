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
    static interface Names {
        byte[] HARD = "hard".getBytes();
        byte[] COMMAND = "command".getBytes();
        byte[] KEY = "key".getBytes();
        byte[] VALUE = "value".getBytes();
        byte[] XT = "xt".getBytes();
        byte[] NUM = "num".getBytes();
        byte[] ORIG = "orig".getBytes();
        byte[] ATOMIC = "atomic".getBytes();
        byte[] MAX = "max".getBytes();
        byte[] OVAL = "oval".getBytes();
        byte[] NVAL = "nval".getBytes();
        byte[] STEP = "step".getBytes();
        byte[] PREFIX = "prefix".getBytes();
        byte[] REGEX = "regex".getBytes();
        byte[] ERROR = "ERROR".getBytes();
        byte[] DB = "DB".getBytes();
        byte[] CUR = "CUR".getBytes();
    }

    protected Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    protected Transcoder valueTranscoder = StringTranscoder.INSTANCE;
    protected String target = null;
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
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("status", createInputWithTarget()));
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(decodeStr(pair.key), decodeStr(pair.value));
        }
        return result;
    }

    public void clear() {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("clear", createInputWithTarget()));
        checkError(response);
    }

    public void synchronize(boolean hard) {
        synchronize(hard, "");
    }

    public void synchronize(boolean hard, String command) {
        Values input = createInputWithTarget();
        input.put(Names.HARD, encodeStr(String.valueOf(hard)));
        input.put(Names.COMMAND, encodeStr(command));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("synchronize", input));
        checkError(response);
    }

    public void set(Object key, Object value) {
        set(key, value, ExpirationTime.NONE);
    }
    
    public void set(Object key, Object value, ExpirationTime xt) {
        Values input = createInputWithTarget();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("set", input));
        checkError(response);
    }

    public void add(Object key, Object value) {
        add(key, value, ExpirationTime.NONE);
    }

    public void add(Object key, Object value, ExpirationTime xt) {
        Values input = createInputWithTarget();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("add", input));
        checkError(response);
    }

    public void replace(Object key, Object value) {
        replace(key, value, ExpirationTime.NONE);
    }

    public void replace(Object key, Object value, ExpirationTime xt) {
        Values input = createInputWithTarget();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("replace", input));
        checkError(response);
    }

    public void append(Object key, Object value) {
        append(key, value, ExpirationTime.NONE);
    }

    public void append(Object key, Object value, ExpirationTime xt) {
        Values input = createInputWithTarget();
        putKeyAndValue(input, key, value);
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("append", input));
        checkError(response);
    }

    public long increment(Object key, long num) {
    	return increment(key, num, IncrementOrigin.ZERO, ExpirationTime.NONE);
    }

    public long increment(Object key, long num, IncrementOrigin orig, ExpirationTime xt) {
        Values input = createInputWithTarget().put(Names.KEY, keyTranscoder.encode(key)).put(Names.NUM, encodeStr(String.valueOf(num)));
        if (orig == IncrementOrigin.SET) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Long.MAX_VALUE)));
        } else if (orig == IncrementOrigin.TRY) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Long.MIN_VALUE)));
        }
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment", input));
        checkError(response);
        return Long.parseLong(decodeStr(response.output.get(Names.NUM)));
    }

    public double incrementDouble(Object key, double num) {
    	return incrementDouble(key, num, IncrementOrigin.ZERO, ExpirationTime.NONE);
    }

    public double incrementDouble(Object key, double num, IncrementOrigin orig, ExpirationTime xt) {
        Values input = createInputWithTarget().put(Names.KEY, keyTranscoder.encode(key)).put(Names.NUM, encodeStr(String.valueOf(num)));
        if (orig == IncrementOrigin.SET) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Double.POSITIVE_INFINITY)));
        } else if (orig == IncrementOrigin.TRY) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Double.NEGATIVE_INFINITY)));
        }
        putExpirationTime(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment_double", input));
        checkError(response);
        return Double.parseDouble(decodeStr(response.output.get(Names.NUM)));
    }

    public boolean cas(Object key, Object oval, Object nval) {
        return cas(key, oval, nval, ExpirationTime.NONE);
    }

    public boolean cas(Object key, Object oval, Object nval, ExpirationTime xt) {
        Values input = createInputWithTarget().put(Names.KEY, keyTranscoder.encode(key));
        if (oval != null) {
            input.put(Names.OVAL, valueTranscoder.encode(oval));
        }
        if (nval != null) {
            input.put(Names.NVAL, valueTranscoder.encode(nval));
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
        Values input = createInputWithTarget().put(Names.KEY, keyTranscoder.encode(key));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("remove", input));
        if (response.status == 450) {
            return false;
        }
        checkError(response);
        return true;
    }

    public Object get(Object key) {
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("get", createInputWithTarget().put(Names.KEY, keyTranscoder.encode(key))));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return valueTranscoder.decode(response.output.get(Names.VALUE));
    }

    public Object seize(Object key) {
        Values input = createInputWithTarget().put(Names.KEY, keyTranscoder.encode(key));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("seize", input));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return valueTranscoder.decode(response.output.get(Names.VALUE));
    }

    public long setBulk(Map<Object, Object> entries) {
        return setBulk(entries, ExpirationTime.NONE, false);
    }

    public long setBulk(Map<Object, Object> entries, ExpirationTime xt, boolean atomic) {
        Values input = createInputWithTarget();
        putExpirationTime(input, xt);
        if (atomic) {
            input.put(Names.ATOMIC, new byte[0]);
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
        return Long.parseLong(decodeStr(response.output.get(Names.NUM)));
    }

    public long removeBulk(List<Object> keys) {
        return removeBulk(keys, false);
    }

    public long removeBulk(List<Object> keys, boolean atomic) {
        Values input = createInputWithTarget();
        if (atomic) {
            input.put(Names.ATOMIC, new byte[0]);
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
        return Long.parseLong(decodeStr(response.output.get(Names.NUM)));
    }

    public Map<Object, Object> getBulk(List<Object> keys) {
        return getBulk(keys, false);
    }

    public Map<Object, Object> getBulk(List<Object> keys, boolean atomic) {
        Values input = createInputWithTarget();
        if (atomic) {
            input.put(Names.ATOMIC, new byte[0]);
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
        if (result.size() != Long.parseLong(decodeStr(response.output.get(Names.NUM)))) {
            throw new AssertionError();
        }
        return result;
    }

    public void vacuum() {
        vacuum(0);
    }

    public void vacuum(long step) {
        Values input = createInputWithTarget();
        if (step > 0) {
            input.put(Names.STEP, encodeStr(String.valueOf(step)));
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("vacuum", input));
        checkError(response);
    }

    public List<Object> matchPrefix(Object prefix) {
        return matchPrefix(prefix, -1);
    }

    public List<Object> matchPrefix(Object prefix, long max) {
        Values input = createInputWithTarget();
        input.put(Names.PREFIX, keyTranscoder.encode(prefix));
        if (max >= 0) {
            input.put(Names.MAX, encodeStr(String.valueOf(max)));
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
        if (result.size() != Long.parseLong(decodeStr(response.output.get(Names.NUM)))) {
            throw new AssertionError();
        }
        return result;
    }

    public List<Object> matchRegex(Object regex) {
        return matchRegex(regex, -1);
    }

    public List<Object> matchRegex(Object regex, long max) {
        Values input = createInputWithTarget();
        input.put(Names.REGEX, keyTranscoder.encode(regex));
        if (max >= 0) {
            input.put(Names.MAX, encodeStr(String.valueOf(max)));
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
        if (result.size() != Long.parseLong(decodeStr(response.output.get(Names.NUM)))) {
            throw new AssertionError();
        }
        return result;
    }

    public void setTarget(String expression) {
        this.target = expression;
    }

    //Utilities

    byte[] encodeStr(String s) {
        return s.getBytes();
    }

    String decodeStr(byte[] b) {
        return new String(b);
    }

    void putKeyAndValue(Values values, Object key, Object value) {
        values.put(Names.KEY, keyTranscoder.encode(key)).put(Names.VALUE, valueTranscoder.encode(value));
    }

    void putExpirationTime(Values values, ExpirationTime xt) {
        if (xt.isEnabled()) {
            values.put(Names.XT, encodeStr(String.valueOf(xt.getValue())));
        }
    }

    void checkError(TsvRpcResponse response) {
        if (response.status == 200) {
            return;
        }
        byte[] error = response.output.get(Names.ERROR);
        String message = (error == null || error.length == 0)
                ? "HTTP Status Code is " + response.status
                : decodeStr(error) + "(" + response.status + ")";
        throw new KyotoTycoonException(message);
    }

    Values createInputWithTarget() {
        Values input = new Values();
        if (target != null) {
            input.put(Names.DB, encodeStr(target));
        }
        return input;
    }

    void setDbParameter(Values input) {
        if (target != null) {
            input.put(Names.DB, encodeStr(target));
        }
    }
}
