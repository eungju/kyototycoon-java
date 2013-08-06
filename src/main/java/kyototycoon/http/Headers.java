package kyototycoon.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Headers implements Iterable<Header> {
    private final List<Header> headers;

    public Headers() {
        headers = new ArrayList<Header>();
    }

    public void addHeader(Header header) {
        headers.add(header);
    }

    public void addHeader(String name, String value) {
        addHeader(new Header(name, value));
    }

    public Iterator<Header> iterator() {
        return headers.iterator();
    }

    public boolean hasHeader(String name) {
        return getHeader(name) != null;
    }

    public Header getHeader(String name) {
        for (Header header : headers) {
            if (header.name.equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public int size() {
        return headers.size();
    }

    public boolean hasContentLength() {
        return hasHeader("Content-Length");
    }

    public int getContentLength() {
        return getHeader("Content-Length").getValueAsInt();
    }

    public boolean isConnectionKeepAlive() {
        Header header = getHeader("Connection");
        return header != null && header.value.equalsIgnoreCase("Keep-Alive");
    }
}
