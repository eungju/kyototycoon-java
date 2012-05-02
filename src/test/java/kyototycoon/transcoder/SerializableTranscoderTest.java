package kyototycoon.transcoder;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class SerializableTranscoderTest extends TranscoderTest {
	@Before public void beforEach() {
		dut = new SerializableTranscoder();
	}
	
	@Test public void encode() {
		assertNotNull(dut.encode("object"));
	}

	@Test public void decode() {
		Serializable serializable = "serializable";
		assertEquals(serializable, dut.decode(dut.encode(serializable)));
	}
}
