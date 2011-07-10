package kyototycoon.networking.jetty;

import kyototycoon.networking.Networking;
import kyototycoon.Values;
import org.eclipse.jetty.client.HttpClient;

import java.net.URI;

public class JettyNetworking implements Networking {
    private URI[] addresses;
    HttpClient httpClient;
    private JettyNode[] nodes;

    public JettyNetworking() {
    }

    public void initialize(URI[] addresses) {
        this.addresses = addresses;
    }

    public void start() throws Exception {
        httpClient = new HttpClient();
        httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        httpClient.start();
        nodes = new JettyNode[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            nodes[i] = new JettyNode(this, addresses[i]);
        }
    }

    public void stop() {
        try {
            httpClient.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Values call(String procedure, Values input) {
        JettyNode node = nodes[0];
        return node.call(procedure, input);
    }
}
