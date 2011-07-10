package kyototycoon.transcoder;

public class ByteArrayTranscoder implements Transcoder<byte[]> {
    public byte[] encode(byte[] decoded) {
        return decoded;
    }

    public byte[] decode(byte[] encoded) {
        return encoded;
    }
}
