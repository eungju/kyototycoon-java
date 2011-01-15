package kyototycoon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class RemoteDBTest {
    private RemoteDB dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new RemoteDB("localhost", 1978);
        dut.clear();
    }

    @After
    public void afterEach() {
        dut.destroy();
    }

    @Test public void getReturnNullWhenTheRecordIsNotExist() {
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void setAndGet() {
        dut.set("key", "value");
        assertThat(dut.get("key"), is((Object) "value"));
    }

    @Test public void storeSpecialCharacters() {
        dut.set("display\tname", "Eungju PARK\n");
        assertThat(dut.get("display\tname"), is((Object) "Eungju PARK\n"));
    }

    @Test public void incrementNotExistingRecord() {
        assertThat(dut.increment("count", 1L), is(1L));
    }

    @Test(expected=IllegalArgumentException.class) public void incrementStringValue() {
        dut.set("count", "3");
        assertThat(dut.increment("count", 4L), is(7L));
    }

    @Test public void incrementDoubleNotExistingRecord() {
        assertThat(dut.incrementDouble("count", 0.1D), is(0.1D));
    }

    @Test(expected=IllegalArgumentException.class) public void incrementDoubleStringValue() {
        dut.set("count", "0.3");
        assertThat(dut.incrementDouble("count", 0.4D), is(0.7D));
    }
}
