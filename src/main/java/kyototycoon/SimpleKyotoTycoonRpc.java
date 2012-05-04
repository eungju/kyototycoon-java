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
        byte[] WAIT = "WAIT".getBytes();
        byte[] WAITTIME = "WAITTIME".getBytes();
        byte[] SIGNAL = "SIGNAL".getBytes();
        byte[] SIGNALBROAD = "SIGNALBROAD".getBytes();
    }

    protected Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    protected Transcoder valueTranscoder = StringTranscoder.INSTANCE;
    protected String target = null;
    protected final SignalWait signalWait = new SignalWait();
    protected final SignalSend signalSend = new SignalSend();
    protected TsvRpc tsvRpc;

    public void setKeyTranscoder(Transcoder transcoder) {
        keyTranscoder = transcoder;
    }

    public void setValueTranscoder(Transcoder transcoder) {
        valueTranscoder = transcoder;
    }

    public Map<String,String> report() {
        Values input = new Values();
        setSignalParameters(input);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("report", input));
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(decodeStr(pair.key), decodeStr(pair.value));
        }
        return result;
    }

    public List<Object> playScript(String name, Map<Object, Object> entries) {
        Values input = new Values();
        setSignalParameters(input);
        input.put("name".getBytes(), encodeStr(name));
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            byte[] key = keyTranscoder.encode(entry.getKey());
            byte[] value = valueTranscoder.encode(entry.getValue());
            byte[] markedKey = new byte[key.length + 1];
            markedKey[0] = '_';
            System.arraycopy(key, 0, markedKey, 1, key.length);
            input.put(markedKey, value);
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("play_script", input));
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
        return result;
    }
    
    public void tuneReplication() {
        tuneReplication(null, 1978, Long.MAX_VALUE, -1);
    }

    public void tuneReplication(String host, int port, long ts, double iv) {
        Values input = new Values();
        setSignalParameters(input);
        if (host != null && !host.isEmpty()) {
            input.put("host".getBytes(), encodeStr(host));
        }
        if (port != 1978) {
            input.put("port".getBytes(), encodeStr(String.format("%d", port)));
        }
        if (ts == Long.MAX_VALUE - 1) {
            input.put("ts".getBytes(), encodeStr("now"));
        } else if (ts != Long.MAX_VALUE) {
            input.put("ts".getBytes(), encodeStr(String.format("%d", ts)));
        }
        if (iv >= 0) {
            input.put("iv".getBytes(), encodeStr(String.format("%.6f", iv)));
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("tune_replication", input));
        checkError(response);
    }

    public Map<String,String> status() {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("status", input));
        checkError(response);
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValuePair pair : response.output) {
            result.put(decodeStr(pair.key), decodeStr(pair.value));
        }
        return result;
    }

    public void clear() {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("clear", input));
        checkError(response);
    }

    public void synchronize(boolean hard) {
        synchronize(hard, "");
    }

    public void synchronize(boolean hard, String command) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        input.put(Names.HARD, encodeStr(String.valueOf(hard)));
        input.put(Names.COMMAND, encodeStr(command));
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("synchronize", input));
        checkError(response);
    }

    public void set(Object key, Object value) {
        set(key, value, ExpirationTime.NONE);
    }
    
    public void set(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        setValueParameter(input, value);
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("set", input));
        checkError(response);
    }

    public void add(Object key, Object value) {
        add(key, value, ExpirationTime.NONE);
    }

    public void add(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        setValueParameter(input, value);
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("add", input));
        checkError(response);
    }

    public void replace(Object key, Object value) {
        replace(key, value, ExpirationTime.NONE);
    }

    public void replace(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        setValueParameter(input, value);
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("replace", input));
        checkError(response);
    }

    public void append(Object key, Object value) {
        append(key, value, ExpirationTime.NONE);
    }

    public void append(Object key, Object value, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        setValueParameter(input, value);
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("append", input));
        checkError(response);
    }

    public long increment(Object key, long num) {
    	return increment(key, num, IncrementOrigin.ZERO, ExpirationTime.NONE);
    }

    public long increment(Object key, long num, IncrementOrigin orig, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        input.put(Names.NUM, encodeStr(String.valueOf(num)));
        if (orig == IncrementOrigin.SET) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Long.MAX_VALUE)));
        } else if (orig == IncrementOrigin.TRY) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Long.MIN_VALUE)));
        }
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment", input));
        checkError(response);
        return Long.parseLong(decodeStr(response.output.get(Names.NUM)));
    }

    public double incrementDouble(Object key, double num) {
    	return incrementDouble(key, num, IncrementOrigin.ZERO, ExpirationTime.NONE);
    }

    public double incrementDouble(Object key, double num, IncrementOrigin orig, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        input.put(Names.NUM, encodeStr(String.valueOf(num)));
        if (orig == IncrementOrigin.SET) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Double.POSITIVE_INFINITY)));
        } else if (orig == IncrementOrigin.TRY) {
            input.put(Names.ORIG, encodeStr(String.valueOf(Double.NEGATIVE_INFINITY)));
        }
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("increment_double", input));
        checkError(response);
        return Double.parseDouble(decodeStr(response.output.get(Names.NUM)));
    }

    public boolean cas(Object key, Object oval, Object nval) {
        return cas(key, oval, nval, ExpirationTime.NONE);
    }

    public boolean cas(Object key, Object oval, Object nval, ExpirationTime xt) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        if (oval != null) {
            input.put(Names.OVAL, valueTranscoder.encode(oval));
        }
        if (nval != null) {
            input.put(Names.NVAL, valueTranscoder.encode(nval));
        }
        setExpirationTimeParameter(input, xt);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("cas", input));
        if (response.status == 450) {
            return false;
        }
        checkError(response);
        return true;
    }

    public boolean remove(Object key) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("remove", input));
        if (response.status == 450) {
            return false;
        }
        checkError(response);
        return true;
    }

    public Object get(Object key) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("get", input));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return getValueParameter(response.output);
    }

    public Object seize(Object key) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setKeyParameter(input, key);
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("seize", input));
        if (response.status == 450) {
            return null;
        }
        checkError(response);
        return getValueParameter(response.output);
    }

    public long setBulk(Map<Object, Object> entries) {
        return setBulk(entries, ExpirationTime.NONE, false);
    }

    public long setBulk(Map<Object, Object> entries, ExpirationTime xt, boolean atomic) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        setExpirationTimeParameter(input, xt);
        if (atomic) {
            input.put(Names.ATOMIC);
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
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        if (atomic) {
            input.put(Names.ATOMIC);
        }
        for (Object key : keys) {
            byte[] bareKey = keyTranscoder.encode(key);
            byte[] markedKey = new byte[bareKey.length + 1];
            markedKey[0] = '_';
            System.arraycopy(bareKey, 0, markedKey, 1, bareKey.length);
            input.put(markedKey);
        }
        TsvRpcResponse response = tsvRpc.call(new TsvRpcRequest("remove_bulk", input));
        checkError(response);
        return Long.parseLong(decodeStr(response.output.get(Names.NUM)));
    }

    public Map<Object, Object> getBulk(List<Object> keys) {
        return getBulk(keys, false);
    }

    public Map<Object, Object> getBulk(List<Object> keys, boolean atomic) {
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
        if (atomic) {
            input.put(Names.ATOMIC);
        }
        for (Object key : keys) {
            byte[] bareKey = keyTranscoder.encode(key);
            byte[] markedKey = new byte[bareKey.length + 1];
            markedKey[0] = '_';
            System.arraycopy(bareKey, 0, markedKey, 1, bareKey.length);
            input.put(markedKey);
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
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
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
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
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
        Values input = new Values();
        setSignalParameters(input);
        setDbParameter(input);
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

    void setDbParameter(Values input) {
        if (target != null) {
            input.put(Names.DB, encodeStr(target));
        }
    }

    void setKeyParameter(Values input, Object key) {
        input.put(Names.KEY, keyTranscoder.encode(key));
    }

    void setValueParameter(Values input, Object value) {
        input.put(Names.VALUE, valueTranscoder.encode(value));
    }

    void setExpirationTimeParameter(Values input, ExpirationTime xt) {
        if (xt.isEnabled()) {
            input.put(Names.XT, encodeStr(String.valueOf(xt.getValue())));
        }
    }

    void setSignalParameters(Values input) {
        if (signalWait.enabled) {
            input.put(Names.WAIT, encodeStr(signalWait.name));
            if (signalWait.timeout > 0) {
                input.put(Names.WAITTIME, encodeStr(String.format("%.6f", signalWait.timeout)));
            }
            signalWait.clear();
        }
        if (signalSend.enabled) {
            input.put(Names.SIGNAL, encodeStr(signalSend.name));
            if (signalSend.broadcast) {
                input.put(Names.SIGNALBROAD);
            }
            signalSend.clear();
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

    Object getKeyParameter(Values output) {
        return keyTranscoder.decode(output.get(Names.KEY));
    }

    Object getValueParameter(Values output) {
        return valueTranscoder.decode(output.get(Names.VALUE));
    }

    ExpirationTime getExpirationTimeParameter(Values output) {
        byte[] bytes = output.get(Names.XT);
        if (bytes == null || bytes.length == 0) {
            return ExpirationTime.NONE;
        }
        return ExpirationTime.at(Long.parseLong(decodeStr(bytes)));
    }

    static class SignalWait {
        public boolean enabled;
        public String name;
        public double timeout;

        public SignalWait() {
            clear();
        }

        public void set(String name, double timeout) {
            this.enabled = true;
            this.name = name;
            this.timeout = timeout;
        }

        public void clear() {
            enabled = false;
            name = null;
            timeout = 0;
        }
    }

    static class SignalSend {
        public boolean enabled;
        public String name;
        public boolean broadcast;

        public SignalSend() {
            clear();
        }

        public void set(String name, boolean broadcast) {
            this.enabled = true;
            this.name = name;
            this.broadcast = broadcast;
        }

        public void clear() {
            enabled = false;
            name = null;
            broadcast = false;
        }
    }
}
