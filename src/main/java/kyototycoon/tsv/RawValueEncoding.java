package kyototycoon.tsv;

public class RawValueEncoding implements ValueEncoding {
    public byte[] encode(byte[] value) {
        return value;
    }

    public byte[] decode(byte[] value) {
        return value;
    }
}
