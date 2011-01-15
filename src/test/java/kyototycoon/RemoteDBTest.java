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
        assertThat(dut.get("key"), is("value"));
    }

    @Test public void storeSpecialCharacters() {
        dut.set("display\tname", "Eungju PARK\n");
        assertThat(dut.get("display\tname"), is("Eungju PARK\n"));
    }
}
