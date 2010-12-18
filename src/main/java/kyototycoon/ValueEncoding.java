package kyototycoon;

import java.io.IOException;

public interface ValueEncoding {
    String encode(String value) throws IOException;

    String decode(String value) throws IOException;
}
