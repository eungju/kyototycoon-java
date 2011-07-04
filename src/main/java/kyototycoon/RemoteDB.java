package kyototycoon;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpVersions;
import org.eclipse.jetty.io.ByteArrayBuffer;

import java.io.IOException;

public class RemoteDB {
    private final HttpClient httpClient;
    private final Address address;
    private final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    private Transcoder keyTranscoder = StringTranscoder.INSTANCE;
    private Transcoder valueTranscoder = StringTranscoder.INSTANCE;

    public RemoteDB(String host, int port) throws Exception {
        this.address = new Address(host, port);
        httpClient = new HttpClient();
        httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        httpClient.start();
    }

    public void destroy() {
        try {
            httpClient.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String key, Object value) {
        call("set", new Values().put("key", keyTranscoder.encode(key)).put("value", valueTranscoder.encode(value)));
    }

    public Object get(String key) {
        Values output = call("get", new Values().put("key", keyTranscoder.encode(key)));
        byte[] value = output.get("value");
        return value == null ? null : valueTranscoder.decode(value);
    }

    public void clear() {
        call("clear", new Values());
    }

    public long increment(String key, long num) {
        Values output = call("increment", new Values().put("key", keyTranscoder.encode(key)).put("num", stringTranscoder.encode(String.valueOf(num))));
        checkError(output);
        return Long.parseLong(stringTranscoder.decode(output.get("num")));
    }

    public double incrementDouble(String key, double num) {
        Values output = call("increment_double", new Values().put("key", keyTranscoder.encode(key)).put("num", stringTranscoder.encode(String.valueOf(num))));
        checkError(output);
        return Double.parseDouble(stringTranscoder.decode(output.get("num")));
    }

    void checkError(Values output) {
        byte[] error = output.get("ERROR");
        if (error != null) {
            throw new RuntimeException(stringTranscoder.decode(error));
        }
    }

    Values call(String command, Values input) {
        TsvEncoding requestEncoding = TsvEncodingHelper.forEfficiency(input);
        try {
            ContentExchange exchange = new ContentExchange(true);
            exchange.setAddress(address);
            exchange.setMethod("POST");
            exchange.setURI("/rpc/" + command);
            exchange.setVersion(HttpVersions.HTTP_1_1);
            exchange.setRequestContentType(requestEncoding.contentType);
            byte[] contentSource = requestEncoding.encode(input);
            exchange.setRequestHeader("Content-Length", String.valueOf(contentSource.length));
            exchange.setRequestContent(new ByteArrayBuffer(contentSource));
            httpClient.send(exchange);
            exchange.waitForDone();
            TsvEncoding responseEncoding = TsvEncodingHelper.forContentType(exchange.getResponseFields().getStringField("Content-Type"));
            Values output;
            if (exchange.getResponseFields().getLongField("Content-Length") != 0) {
                output = responseEncoding.decode(exchange.getResponseContentBytes());
            } else {
                output = new Values();
            }
            if (exchange.getResponseStatus() != 200 && exchange.getResponseStatus() != 450) {
                throw new RuntimeException(stringTranscoder.decode(output.get("ERROR")));
            }
            return output;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
