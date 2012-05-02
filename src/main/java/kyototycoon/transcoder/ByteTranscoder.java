package kyototycoon.transcoder;

import java.util.Arrays;

public class ByteTranscoder implements Transcoder<Byte> {
	public byte[] encode(Byte decoded) {
        return new byte[] { decoded };
	}

	public Byte decode(byte[] encoded) {
		if (encoded.length != Byte.SIZE / 8) {
			throw new IllegalArgumentException("Unable to decode " + Arrays.toString(encoded));
		}
		return encoded[0];
	}
}
