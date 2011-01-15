package kyototycoon;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpVersions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RemoteDB {
    private final HttpClient httpClient;
    private final String host;
    private final int port;

    public RemoteDB(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
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

    public void set(String key, String value) {
        call("set", new Values().put("key", key).put("value", value));
    }


    public String get(String key) {
        Values output = call("get", new Values().put("key", key));
        return output.get("value");
    }

    public void clear() {
        call("clear", new Values());
    }

    Values call(String command, Values input) {
        TsvEncoding requestEncoding = TsvEncodingHelper.forEfficiency(input);
        try {
            ContentExchange exchange = new ContentExchange(true);
            exchange.setAddress(new Address(host, port));
            exchange.setMethod("POST");
            exchange.setURI("/rpc/" + command);
            exchange.setVersion(HttpVersions.HTTP_1_1);
            exchange.setRequestContentType(requestEncoding.contentType);
            byte[] contentSource = requestEncoding.encode(input);
            exchange.setRequestHeader("Content-Length", String.valueOf(contentSource.length));
            exchange.setRequestContentSource(new ByteArrayInputStream(contentSource));
            httpClient.send(exchange);
            exchange.waitForDone();
            TsvEncoding responseEncoding = TsvEncodingHelper.forContentType(exchange.getResponseFields().getStringField("Content-Type"));
            Values output = new Values();
            if (exchange.getResponseFields().getLongField("Content-Length") != 0) {
                output = responseEncoding.decode(exchange.getResponseContentBytes());
            }
            if (exchange.getResponseStatus() != 200 && exchange.getResponseStatus() != 450) {
                throw new RuntimeException(output.get("ERROR"));
            }
            return output;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
