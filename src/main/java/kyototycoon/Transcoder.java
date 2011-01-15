package kyototycoon;

public interface Transcoder<T> {
    byte[] encode(T decoded);

    T decode(byte[] encoded);
}
