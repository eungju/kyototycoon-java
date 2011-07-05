package kyototycoon;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpVersions;
import org.eclipse.jetty.io.ByteArrayBuffer;

import java.io.IOException;

public class TsvRpcConnection {
    private final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    private final HttpClient httpClient;
    private final Address address;

    public TsvRpcConnection(String host, int port) throws Exception {
        this.address = new Address(host, port);
        httpClient = new HttpClient();
        httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        httpClient.start();
    }

    public void close() {
        try {
            httpClient.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Values call(String command, Values input) {
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
