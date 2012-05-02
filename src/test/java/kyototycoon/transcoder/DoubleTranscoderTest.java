package kyototycoon.transcoder;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.*;

public class DoubleTranscoderTest extends TranscoderTest {

	@Before public void beforeEach() {
		dut = new DoubleTranscoder(ByteOrder.BIG_ENDIAN);
	}
	
	@Test public void encode() {
		assertArrayEquals(new byte[] {0x3F, (byte) 0xB9, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x9A}, dut.encode(0.1D));
	}
	
	@Test public void decode() {
		assertEquals(0.1D, dut.decode(new byte[] {0x3F, (byte) 0xB9, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x9A}));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotDecodeInvalid() {
		dut.decode(new byte[] {0x01});
	}
}
