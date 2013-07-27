package kyototycoon.http;

public interface HttpMessageCoder {
    byte[] SP = " ".getBytes();
    byte[] COLON = ":".getBytes();
    byte[] CRLF = "\r\n".getBytes();
}
