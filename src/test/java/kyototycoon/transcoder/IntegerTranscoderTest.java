package kyototycoon.transcoder;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.*;

public class IntegerTranscoderTest extends TranscoderTest {
	@Before public void beforeEach() {
		dut = new IntegerTranscoder(ByteOrder.BIG_ENDIAN);
	}
	
	@Test public void encode() {
		assertArrayEquals(new byte[] {0x12, 0x34, 0x56, 0x78}, dut.encode(0x12345678));
	}
	
	@Test public void decode() {
		assertEquals(0x12345678, dut.decode(new byte[] {0x12, 0x34, 0x56, 0x78}));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotDecodeInvalid() {
		dut.decode(new byte[] {0x12, 0x34, 0x56, 0x78, (byte) 0x90});
	}
}
