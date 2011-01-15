package kyototycoon;

import java.io.IOException;

public interface ValueEncoding {
    byte[] encode(byte[] value) throws IOException;

    byte[] decode(byte[] value) throws IOException;
}
