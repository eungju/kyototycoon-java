package kyototycoon.transcoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class DoubleTranscoder implements Transcoder<Double> {
	private final ByteOrder byteOrder;

	public DoubleTranscoder() {
		this(ByteOrder.nativeOrder());
	}
	
	public DoubleTranscoder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	public byte[] encode(Double decoded) {
		return ByteBuffer.allocate(Double.SIZE / 8).order(byteOrder).putDouble(decoded).array();
	}

	public Double decode(byte[] encoded) {
		if (encoded.length != Double.SIZE / 8) {
			throw new IllegalArgumentException("Unable to decode " + Arrays.toString(encoded));
		}
		return ByteBuffer.wrap(encoded).order(byteOrder).getDouble();
	}
}
