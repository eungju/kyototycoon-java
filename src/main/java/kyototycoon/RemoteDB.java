package kyototycoon;

import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpVersions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

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
        call("set", ImmutableMap.of("key", key, "value", value));
    }


    public String get(String key) {
        Map<String, String> output = call("get", ImmutableMap.of("key", key));
        return output.get("value");
    }

    Map<String, String> call(String command, Map<String, String> input) {
        try {
            ContentExchange exchange = createExchange(command, input);
            httpClient.send(exchange);
            exchange.waitForDone();
            RawTsvEncoding encoding = new RawTsvEncoding();
            Map<String, String> output = ImmutableMap.of();
            if (exchange.getResponseFields().getLongField("Content-Length") != 0) {
                output = encoding.decode(exchange.getResponseContentBytes());
            }
            if (exchange.getResponseStatus() != 200) {
                throw new RuntimeException(output.get("ERROR"));
            }
            return output;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ContentExchange createExchange(String command, Map<String, String> input) {
        ContentExchange exchange = new ContentExchange(true);
        exchange.setAddress(new Address(host, port));
        exchange.setMethod("POST");
        exchange.setURI("/rpc/" + command);
        exchange.setVersion(HttpVersions.HTTP_1_1);
        exchange.setRequestContentType("text/tab-separated-values");
        byte[] contentSource = new RawTsvEncoding().encode(input);
        exchange.setRequestHeader("Content-Length", String.valueOf(contentSource.length));
        exchange.setRequestContentSource(new ByteArrayInputStream(contentSource));
        return exchange;
    }
}
