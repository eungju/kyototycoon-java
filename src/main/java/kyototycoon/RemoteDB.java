package kyototycoon;

import kyototycoon.networking.Networking;
import kyototycoon.networking.netty.NettyNetworking;
import kyototycoon.transcoder.StringTranscoder;
import kyototycoon.transcoder.Transcoder;
import kyototycoon.tsv.Values;

import java.net.URI;

public class RemoteDB implements KyotoTycoonClient {
    private final Networking networking;
    private final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    private Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    private Transcoder valueTranscoder = StringTranscoder.INSTANCE;

    public RemoteDB(URI[] addresses) throws Exception {
        networking = new NettyNetworking();
        networking.initialize(addresses);
        networking.start();
    }

    public void close() {
        networking.stop();
    }

    public void setValueTranscoder(Transcoder transcoder) {
        valueTranscoder = transcoder;
    }

    public void set(String key, Object value) {
        networking.call("set", new Values().put(KEY, keyTranscoder.encode(key)).put(VALUE, valueTranscoder.encode(value)));
    }

    public Object get(String key) {
        Values output = networking.call("get", new Values().put(KEY, keyTranscoder.encode(key)));
        byte[] value = output.get(VALUE);
        return value == null ? null : valueTranscoder.decode(value);
    }

    public void clear() {
        networking.call("clear", new Values());
    }

    public long increment(String key, long num) {
        Values output = networking.call("increment", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num))));
        checkError(output);
        return Long.parseLong(stringTranscoder.decode(output.get(NUM)));
    }

    public double incrementDouble(String key, double num) {
        Values output = networking.call("increment_double", new Values().put(KEY, keyTranscoder.encode(key)).put(NUM, stringTranscoder.encode(String.valueOf(num))));
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
