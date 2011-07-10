package kyototycoon.networking.jetty;

import kyototycoon.transcoder.StringTranscoder;
import kyototycoon.tsv.Values;
import kyototycoon.networking.Node;
import kyototycoon.tsv.TsvEncoding;
import kyototycoon.tsv.TsvEncodingHelper;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.http.HttpVersions;
import org.eclipse.jetty.io.ByteArrayBuffer;

import java.io.IOException;
import java.net.URI;

public class JettyNode implements Node {
    private final StringTranscoder stringTranscoder = StringTranscoder.INSTANCE;
    private JettyNetworking networking;
    private URI address;

    public JettyNode(JettyNetworking networking, URI address) {
        this.networking = networking;
        this.address = address;
    }

    public Values call(String procedure, Values input) {
        TsvEncoding requestEncoding = TsvEncodingHelper.forEfficiency(input);
        try {
            ContentExchange exchange = new ContentExchange(true);
            exchange.setURL(address.resolve("/rpc/" + procedure).toASCIIString());
            exchange.setMethod("POST");
            exchange.setVersion(HttpVersions.HTTP_1_1);
            exchange.setRequestContentType(requestEncoding.contentType);
            byte[] contentSource = requestEncoding.encode(input);
            exchange.setRequestHeader("Content-Length", String.valueOf(contentSource.length));
            exchange.setRequestContent(new ByteArrayBuffer(contentSource));
            networking.httpClient.send(exchange);
            exchange.waitForDone();
            TsvEncoding responseEncoding = TsvEncodingHelper.forContentType(exchange.getResponseFields().getStringField("Content-Type"));
            Values output;
            if (exchange.getResponseFields().getLongField("Content-Length") > 0) {
                output = responseEncoding.decode(exchange.getResponseContentBytes());
            } else {
                output = new Values();
            }
            if (exchange.getResponseStatus() != 200 && exchange.getResponseStatus() != 450) {
                throw new RuntimeException(stringTranscoder.decode(output.get("ERROR".getBytes())));
            }
            return output;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
