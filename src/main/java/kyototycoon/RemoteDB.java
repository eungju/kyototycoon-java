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
        Values output = call("increment", new Values().put("key", keyTranscoder.encode(key)).put("num", StringTranscoder.INSTANCE.encode(String.valueOf(num))));
        byte[] error = output.get("ERROR");
        if (error != null) {
            throw new IllegalArgumentException(StringTranscoder.INSTANCE.decode(error));
        }
        return Long.parseLong(StringTranscoder.INSTANCE.decode(output.get("num")));
    }

    public double incrementDouble(String key, double num) {
        Values output = call("increment_double", new Values().put("key", keyTranscoder.encode(key)).put("num", StringTranscoder.INSTANCE.encode(String.valueOf(num))));
        byte[] error = output.get("ERROR");
        if (error != null) {
            throw new IllegalArgumentException(StringTranscoder.INSTANCE.decode(error));
        }
        return Double.parseDouble(StringTranscoder.INSTANCE.decode(output.get("num")));
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
                throw new RuntimeException(StringTranscoder.INSTANCE.decode(output.get("ERROR")));
            }
            return output;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
