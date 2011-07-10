package kyototycoon.transcoder;

public interface Transcoder<T> {
    byte[] encode(T decoded);

    T decode(byte[] encoded);
}
