package kyototycoon.transcoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class SerializableTranscoder implements Transcoder<Object> {
	public byte[] encode(Object decoded) {
		if (decoded == null) {
			throw new NullPointerException("Cannot encode null");
		}
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(decoded);
			os.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to encode " + decoded, e);
		}
	}

	public Object decode(byte[] encoded) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
			ObjectInputStream is = new ObjectInputStream(bis);
			Object decoded = is.readObject();
			is.close();
			bis.close();
			return decoded;
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to decode " + Arrays.toString(encoded), e);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unable to decode " + Arrays.toString(encoded), e);
		}
	}
}
