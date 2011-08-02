package kyototycoon;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class KyotoTycoonIntegrationTest {
    private SimpleKyotoTycoonClient dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new SimpleKyotoTycoonClient();
        dut.setHosts(Arrays.asList(KyotoTycoonFixture.SERVER_ADDRESS));
        dut.start();
        dut.clear();
    }

    @After
    public void afterEach() {
        dut.stop();
    }

    @Test public void get_the_report_of_the_server_information() {
        Map<String, String> actual = dut.report();
        System.out.println(actual);
        assertThat(actual, Matchers.<Object>notNullValue());
    }

    @Test public void get_the_miscellaneous_status_information_of_a_database() {
        Map<String, String> actual = dut.status();
        System.out.println(actual);
        assertThat(actual, Matchers.<Object>notNullValue());
    }

    @Test public void remove_all_records_in_a_database() {
        dut.set("key", "value");
        dut.clear();
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void synchronize_updated_contents_with_the_file_and_the_device() {
        dut.synchronize(true);
    }

    @Test public void set_the_value_of_a_record() {
        dut.set("key", "value");
        assertThat((String) dut.get("key"), is("value"));
    }

    @Test public void add_a_record() {
        dut.add("key", "value");
        assertThat((String) dut.get("key"), is("value"));
    }

    @Test(expected=RuntimeException.class) public void add_a_existing_record() {
        dut.add("key", "value");
        dut.add("key", "value");
    }

    @Test public void replace_the_value_of_a_record() {
        dut.add("key", "1");
        dut.replace("key", "2");
        assertThat((String) dut.get("key"), is("2"));
    }

    @Test(expected=RuntimeException.class)
    public void replace_the_value_of_a_non_existing_record() {
        dut.replace("key", "value");
    }

    @Test public void append_the_value_of_a_record() {
        dut.add("key", "1");
        dut.append("key", "23");
        assertThat((String) dut.get("key"), is("123"));
    }

    @Test public void append_the_value_of_a_non_existing_record() {
        dut.append("key", "23");
        assertThat((String) dut.get("key"), is("23"));
    }
    
    @Test public void
    add_a_numeric_integer_to_the_numeric_integer_value_of_a_record() {
    	dut.increment("key", 3L, IncrementOrigin.ZERO, ExpirationTime.NONE);
    	assertThat(dut.increment("key", 4), is(7L));
    }

    @Test public void
    add_a_numeric_integer_value_to_a_non_existing_record() {
    	assertThat(dut.increment("key", 3), is(3L));
    }

    @Test(expected=RuntimeException.class) public void
    add_a_numeric_integer_value_to_the_non_numeric_integer_value_of_a_record() {
        dut.set("key", "3");
        dut.increment("key", 4L);
    }

    @Test public void
    set_the_numeric_integer_value_of_a_record() {
        dut.increment("key", 3L);
        assertThat(dut.increment("key", 4L, IncrementOrigin.SET, ExpirationTime.NONE), is(4L));
    }

    @Test(expected=RuntimeException.class) public void
    try_to_set_the_numeric_integer_value_of_a_record() {
        dut.increment("key", 4L, IncrementOrigin.TRY, ExpirationTime.NONE);
    }

    @Test public void
    add_a_numeric_double_value_to_the_numeric_double_value_of_a_record() {
    	dut.incrementDouble("key", 0.3, IncrementOrigin.ZERO, ExpirationTime.NONE);
    	assertThat(dut.incrementDouble("key", 0.4), is(0.7));
    }

    @Test public void
    add_a_numeric_double_value_to_a_non_existing_record() {
    	assertThat(dut.incrementDouble("key", 0.3), is(0.3));
    }

    @Test(expected=RuntimeException.class) public void
    add_a_numeric_double_value_to_the_non_numeric_double_value_of_a_record() {
        dut.set("key", "0.3");
        dut.incrementDouble("key", 0.4);
    }

    @Test public void
    set_the_numeric_double_value_of_a_record() {
        dut.incrementDouble("key", 0.3);
        assertThat(dut.incrementDouble("key", 0.4, IncrementOrigin.SET, ExpirationTime.NONE), is(0.4));
    }

    @Test(expected=RuntimeException.class) public void
    try_to_set_the_double_value_of_a_record() {
        dut.incrementDouble("key", 0.4, IncrementOrigin.TRY, ExpirationTime.NONE);
    }

    @Test public void
    compareAndSwap() {
        dut.set("key", "1");
        assertThat(dut.cas("key", "1", "2"), is(true));
        assertThat((String) dut.get("key"), is("2"));
        assertThat(dut.cas("key", "1", "3"), is(false));
        assertThat((String) dut.get("key"), is("2"));
    }

    @Test public void
    compareAndSwapRemovesTheRecord() {
        dut.set("key", "1");
        dut.cas("key", "1", null);
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void remove_a_record() {
        dut.set("key", "value");
        assertThat(dut.remove("key"), is(true));
    }

    @Test public void remove_a_non_existing_record() {
        assertThat(dut.remove("key"), is(false));
    }

    @Test public void
    get_retrieves_the_value_of_a_record() {
        dut.set("key", "value");
        assertThat((String) dut.get("key"), is("value"));
    }

    @Test public void
    get_returns_null_when_a_record_is_not_exist() {
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    seize_retrieves_the_value_of_a_record_and_remove_it_atomically() {
        dut.set("key", "value");
        assertThat((String) dut.seize("key"), is("value"));
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    seize_returns_null_when_a_record_is_not_exist() {
        assertThat((String) dut.seize("key"), nullValue());
        assertThat(dut.get("key"), nullValue());
    }
}
