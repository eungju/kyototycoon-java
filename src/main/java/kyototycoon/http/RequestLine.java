package kyototycoon.http;

public class RequestLine {
    public final String method;
    public final String uri;
    public final String version;

    public RequestLine(String method, String uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }
}
