package kyototycoon.http;

public class StatusLine {
    public final String version;
    public final int code;
    public final String reason;

    public StatusLine(String version, int code, String reason) {
        this.version = version;
        this.code = code;
        this.reason = reason;
    }
}
