package kyototycoon.transcoder;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.*;

public class FloatTranscoderTest extends TranscoderTest {
	@Before public void beforeEach() {
		dut = new FloatTranscoder(ByteOrder.BIG_ENDIAN);
	}
	
	@Test public void encode() {
		assertArrayEquals(new byte[] {0x3D, (byte) 0xCC, (byte) 0xCC, (byte) 0xCD}, dut.encode(0.1F));
	}
	
	@Test public void decode() {
		assertEquals(0.1F, dut.decode(new byte[] {0x3D, (byte) 0xCC, (byte) 0xCC, (byte) 0xCD}));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotDecodeInvalid() {
		dut.decode(new byte[] {0x01});
	}
}
