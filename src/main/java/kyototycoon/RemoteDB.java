package kyototycoon;

import com.google.common.collect.ImmutableList;
import kyototycoon.finagle.FinagleTsvRpcClient;
import kyototycoon.transcoder.StringTranscoder;
import kyototycoon.transcoder.Transcoder;
import kyototycoon.tsv.TsvRpcRequest;
import kyototycoon.tsv.Values;

import java.net.URI;

public class RemoteDB implements KyotoTycoonClient {
    private final TsvRpcClient client;
    private final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    private Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    private Transcoder valueTranscoder = StringTranscoder.INSTANCE;

    public RemoteDB(URI[] addresses) throws Exception {
        client = new FinagleTsvRpcClient();
        client.setHosts(ImmutableList.copyOf(addresses));
        client.start();
    }

    public void close() {
        client.stop();
    }

    public void setValueTranscoder(Transcoder transcoder) {
        valueTranscoder = transcoder;
    }

    public void set(String key, Object value) {
        client.call(new TsvRpcRequest("set", new Values().put(KEY, keyTranscoder.encode(key)).put(VALUE, valueTranscoder.encode(value))));
    }

    public Object get(String key) {
        Values output = client.call(new TsvRpcRequest("get", new Values().put(KEY, keyTranscoder.encode(key)))).output;
        byte[] value = output.get(VALUE);
        return value == null ? null : valueTranscoder.decode(value);
    }

    public void clear() {
        client.call(new TsvRpcRequest("clear", new Values()));
    }

    public long increment(String key, long num) {
        Values output = client.call(new TsvRpcRequest("increment", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num))))).output;
        checkError(output);
        return Long.parseLong(stringTranscoder.decode(output.get(NUM)));
    }

    public double incrementDouble(String key, double num) {
        Values output = client.call(new TsvRpcRequest("increment_double", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num))))).output;
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
