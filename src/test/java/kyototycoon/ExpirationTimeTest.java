package kyototycoon;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ExpirationTimeTest {
	@Test
	public void none() {
		ExpirationTime dut = ExpirationTime.NONE;
		assertThat(dut.isEnabled(), is(false));
		assertThat(dut.getValue(), is(Long.MAX_VALUE));
	}

	@Test
	public void after() {
		ExpirationTime dut = ExpirationTime.after(1);
		assertThat(dut.isEnabled(), is(true));
		assertThat(dut.getValue(), is(1L));
	}

	@Test
	public void at() {
		ExpirationTime dut = ExpirationTime.at(1);
		assertThat(dut.isEnabled(), is(true));
		assertThat(dut.getValue(), is(-1L));
	}
}
