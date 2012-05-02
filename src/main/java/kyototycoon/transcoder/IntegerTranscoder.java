package kyototycoon.transcoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class IntegerTranscoder implements Transcoder<Integer> {
	public static final IntegerTranscoder INSTANCE = new IntegerTranscoder();
	
	private final ByteOrder byteOrder;

	public IntegerTranscoder() {
		this(ByteOrder.nativeOrder());
	}
	
	public IntegerTranscoder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}
	
	public byte[] encode(Integer decoded) {
		return ByteBuffer.allocate(Integer.SIZE / 8).order(byteOrder).putInt(decoded).array();
	}

	public Integer decode(byte[] encoded) {
		if (encoded.length != Integer.SIZE / 8) {
			throw new IllegalArgumentException("Unable to decode " + Arrays.toString(encoded));
		}
		return ByteBuffer.wrap(encoded).order(byteOrder).getInt();
	}
}
