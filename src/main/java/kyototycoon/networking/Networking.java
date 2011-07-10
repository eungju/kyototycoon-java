package kyototycoon.networking;

import kyototycoon.Values;

import java.net.URI;

public interface Networking {
    void initialize(URI[] addresses);
    
    void start() throws Exception;
    
    void stop();

    Values call(String procedure, Values input);
}
