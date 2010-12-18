package kyototycoon;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlValueEncoding implements ValueEncoding {
    public String encode(String value) throws IOException {
        return URLEncoder.encode(value, "UTF-8");
    }

    public String decode(String value) throws IOException {
        return URLDecoder.decode(value, "UTF-8");
    }
}
