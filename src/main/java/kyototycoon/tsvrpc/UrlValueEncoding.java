package kyototycoon.tsvrpc;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class UrlValueEncoding implements ValueEncoding {
    private Charset encoding = Charsets.UTF_8;

    public byte[] encode(byte[] value) throws IOException {
        //FIXME
        return URLEncoder.encode(new String(value, encoding), encoding.name()).getBytes(encoding);
    }

    public byte[] decode(byte[] value) throws IOException {
        //FIXME
        return URLDecoder.decode(new String(value, encoding), encoding.name()).getBytes(encoding);
    }
}
