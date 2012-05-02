package kyototycoon.transcoder;

import com.google.common.io.Closeables;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [flag:1][body:*]
 * flag: A 8-bit integer standing for the flag. MSB is compression mark. 6-0 bit is the type of the object.
 * body: Arbitary data of the object.
 */
public class SerializingTranscoder implements Transcoder<Object> {
	static final byte TYPE_STRING = 0;
	static final byte TYPE_BOOLEAN = 1;
	static final byte TYPE_INTEGER = 2;
	static final byte TYPE_LONG = 3;
	static final byte TYPE_DATE = 4;
	static final byte TYPE_BYTE = 5;
	static final byte TYPE_FLOAT = 6;
	static final byte TYPE_DOUBLE = 7;
	static final byte TYPE_BYTEARRAY = 8;
	static final byte TYPE_SERIALIZABLE = Byte.MAX_VALUE;
	static final byte COMPRESSED = (byte) 0x80;

	/**
     * Use network byte order.
     */
	private final ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	private final StringTranscoder stringTranscoder = new StringTranscoder();
    private final ByteTranscoder byteTranscoder = new ByteTranscoder();
	private final IntegerTranscoder integerTranscoder = new IntegerTranscoder(byteOrder);
	private final LongTranscoder longTranscoder = new LongTranscoder(byteOrder);
	private final FloatTranscoder floatTranscoder = new FloatTranscoder(byteOrder);
	private final DoubleTranscoder doubleTranscoder = new DoubleTranscoder(byteOrder);
    private final ByteArrayTranscoder byteArrayTranscoder = new ByteArrayTranscoder();
	private final SerializableTranscoder serializableTranscoder = new SerializableTranscoder();
	private final int compressionThreshold;
	
	public SerializingTranscoder() {
		this(16 * 1024);
	}
	
	public SerializingTranscoder(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
	}
	
	public byte[] encode(Object decoded) {
		byte flag;
		byte[] body;
		if (decoded instanceof String) {
			flag = TYPE_STRING;
			body = stringTranscoder.encode((String) decoded);
		} else if (decoded instanceof Boolean) {
			flag = TYPE_BOOLEAN;
			body = encodeBoolean((Boolean) decoded);
		} else if (decoded instanceof Integer) {
			flag = TYPE_INTEGER;
			body = integerTranscoder.encode((Integer) decoded);
		} else if (decoded instanceof Long) {
			flag = TYPE_LONG;
			body = longTranscoder.encode((Long) decoded);
		} else if (decoded instanceof Date) {
			flag = TYPE_DATE;
			body = encodeDate((Date) decoded);
		} else if (decoded instanceof Byte) {
			flag = TYPE_BYTE;
			body = byteTranscoder.encode((Byte) decoded);
		} else if (decoded instanceof Float) {
			flag = TYPE_FLOAT;
			body = floatTranscoder.encode((Float) decoded);
		} else if (decoded instanceof Double) {
			flag = TYPE_DOUBLE;
			body = doubleTranscoder.encode((Double) decoded);
		} else if (decoded instanceof byte[]) {
			flag = TYPE_BYTEARRAY;
			body = byteArrayTranscoder.encode((byte[]) decoded);
		} else {
			flag = TYPE_SERIALIZABLE;
			body = serializableTranscoder.encode(decoded);
		}
		if (compressionThreshold > 0 && body.length > compressionThreshold) {
			byte[] compressed = compress(body);
			if (compressed.length < body.length) {
				flag |= COMPRESSED;
				body = compressed;
			}
		}
		ByteBuffer buf = ByteBuffer.allocate(1 + body.length);
		buf.put(flag);
		buf.put(body);
		return buf.array();
	}

	public Object decode(byte[] encoded) {
		Object decoded;
		ByteBuffer buf = ByteBuffer.wrap(encoded);
		byte flag = buf.get();
		byte[] body = new byte[buf.remaining()];
		System.arraycopy(buf.array(), 1, body, 0, body.length);
		if ((flag & COMPRESSED) != 0) {
			body = decompress(body);
		}
		switch (flag & ~COMPRESSED) {
		case TYPE_STRING:
			decoded = stringTranscoder.decode(body);
			break;
		case TYPE_BOOLEAN:
			decoded = decodeBoolean(body);
			break;
		case TYPE_INTEGER:
			decoded = integerTranscoder.decode(body);
			break;
		case TYPE_LONG:
			decoded = longTranscoder.decode(body);
			break;
		case TYPE_DATE:
			decoded = decodeDate(body);
			break;
		case TYPE_BYTE:
			decoded = byteTranscoder.decode(body);
			break;
		case TYPE_FLOAT:
			decoded = floatTranscoder.decode(body);
			break;
		case TYPE_DOUBLE:
			decoded = doubleTranscoder.decode(body);
			break;
		case TYPE_BYTEARRAY:
			decoded = byteArrayTranscoder.decode(body);
			break;
		default:
			decoded = serializableTranscoder.decode(body);
		}
		return decoded;
	}
	
	byte[] encodeBoolean(boolean value) {
		return byteTranscoder.encode(((byte) (value ? 1 : 0)));		
	}
	
	Boolean decodeBoolean(byte[] body) {
		return byteTranscoder.decode(body) == 0 ? false : true;
	}
	
	byte[] encodeDate(Date value) {
		return longTranscoder.encode((value).getTime());
	}
	
	Date decodeDate(byte[] body) {
		return new Date(longTranscoder.decode(body));
	}

	byte[] compress(byte[] data) {
		ByteArrayOutputStream buffer = null;
		GZIPOutputStream gzip = null;
		try {
			buffer = new ByteArrayOutputStream();
			gzip = new GZIPOutputStream(buffer);
			gzip.write(data);
		} catch (IOException e) {
			throw new RuntimeException("Unable to compress data", e);
		} finally {
			Closeables.closeQuietly(gzip);
            Closeables.closeQuietly(buffer);
		}
		return buffer.toByteArray();
	}

	byte[] decompress(byte[] data) {
		ByteArrayOutputStream buffer = null;
		GZIPInputStream gzip = null;
		try {
			buffer = new ByteArrayOutputStream();
			gzip = new GZIPInputStream(new ByteArrayInputStream(data));
			copy(gzip, buffer);
		} catch (IOException e) {
			throw new RuntimeException("Unable to decompress data", e);
		} finally {
            Closeables.closeQuietly(gzip);
            Closeables.closeQuietly(buffer);
		}
		return buffer.toByteArray();
	}
	
	private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
	}
}
