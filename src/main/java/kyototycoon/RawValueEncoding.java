package kyototycoon;

public class RawValueEncoding implements ValueEncoding {
    public String encode(String value) {
        return value;
    }

    public String decode(String value) {
        return value;
    }
}
