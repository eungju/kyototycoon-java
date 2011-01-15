package kyototycoon;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlValueEncoding implements ValueEncoding {
    public byte[] encode(byte[] value) throws IOException {
        //FIXME
        return URLEncoder.encode(new String(value, "UTF-8"), "UTF-8").getBytes("UTF-8");
    }

    public byte[] decode(byte[] value) throws IOException {
        //FIXME
        return URLDecoder.decode(new String(value, "UTF-8"), "UTF-8").getBytes("UTF-8");
    }
}
