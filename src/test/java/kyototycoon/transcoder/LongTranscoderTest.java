package kyototycoon.transcoder;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.*;

public class LongTranscoderTest extends TranscoderTest {
	@Before public void beforeEach() {
		dut = new LongTranscoder(ByteOrder.BIG_ENDIAN);
	}
	
	@Test public void encode() {
		assertArrayEquals(new byte[] {0x12, 0x34, 0x56, 0x78, 0x12, 0x34, 0x56, 0x78}, dut.encode(0x1234567812345678L));
	}
	
	@Test public void decode() {
		assertEquals(0x1234567812345678L, dut.decode(new byte[] {0x12, 0x34, 0x56, 0x78, 0x12, 0x34, 0x56, 0x78}));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotDecodeInvalid() {
		dut.decode(new byte[] {0x12, 0x34, 0x56, 0x78, 0x12, 0x34, 0x56, 0x78, (byte) 0x90});
	}
}
