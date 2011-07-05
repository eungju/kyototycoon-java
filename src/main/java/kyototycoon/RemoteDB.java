package kyototycoon;

public class RemoteDB {
    private final TsvRpcConnection connection;
    private final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    private Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    private Transcoder valueTranscoder = StringTranscoder.INSTANCE;

    public RemoteDB(String host, int port) throws Exception {
        connection = new TsvRpcConnection(host, port);
    }

    public void destroy() {
        connection.close();
    }

    public void set(String key, Object value) {
        connection.call("set", new Values().put("key", keyTranscoder.encode(key)).put("value", valueTranscoder.encode(value)));
    }

    public Object get(String key) {
        Values output = connection.call("get", new Values().put("key", keyTranscoder.encode(key)));
        byte[] value = output.get("value");
        return value == null ? null : valueTranscoder.decode(value);
    }

    public void clear() {
        connection.call("clear", new Values());
    }

    public long increment(String key, long num) {
        Values output = connection.call("increment", new Values().put("key", keyTranscoder.encode(key)).put("num", stringTranscoder.encode(String.valueOf(num))));
        checkError(output);
        return Long.parseLong(stringTranscoder.decode(output.get("num")));
    }

    public double incrementDouble(String key, double num) {
        Values output = connection.call("increment_double", new Values().put("key", keyTranscoder.encode(key)).put("num", stringTranscoder.encode(String.valueOf(num))));
        checkError(output);
        return Double.parseDouble(stringTranscoder.decode(output.get("num")));
    }

    void checkError(Values output) {
        byte[] error = output.get("ERROR");
        if (error != null) {
            throw new RuntimeException(stringTranscoder.decode(error));
        }
    }
}
