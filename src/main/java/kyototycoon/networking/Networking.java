package kyototycoon.networking;

import kyototycoon.tsv.Values;

import java.net.URI;

public interface Networking {
    void initialize(URI[] addresses);
    
    void start() throws Exception;
    
    void stop();

    Values call(String procedure, Values input);
}
