package kyototycoon.http;

public class HttpException extends RuntimeException {
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
