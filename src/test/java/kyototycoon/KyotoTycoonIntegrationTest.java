package kyototycoon;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KyotoTycoonIntegrationTest {
    private SimpleKyotoTycoonClient dut;

    @Before
    public void beforeEach() throws Exception {
        dut = new SimpleKyotoTycoonClient(Arrays.asList(KyotoTycoonFixture.SERVER_ADDRESS));
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
    	dut.increment("key", 3L, Long.MAX_VALUE, Long.MAX_VALUE);
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
        assertThat(dut.increment("key", 4L, Long.MAX_VALUE, Long.MAX_VALUE), is(4L));
    }

    @Test(expected=RuntimeException.class) public void
    try_to_set_the_numeric_integer_value_of_a_record() {
        assertThat(dut.increment("key", 4L, Long.MIN_VALUE, Long.MAX_VALUE), is(4L));
    }

    @Test public void
    add_a_numeric_double_value_to_the_numeric_double_value_of_a_record() {
    	dut.incrementDouble("key", 0.3, Double.POSITIVE_INFINITY, Long.MAX_VALUE);
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
        assertThat(dut.incrementDouble("key", 0.4, Double.POSITIVE_INFINITY, Long.MAX_VALUE), is(0.4));
    }

    @Test(expected=RuntimeException.class) public void
    try_to_set_the_double_value_of_a_record() {
        assertThat(dut.incrementDouble("key", 0.4, Double.NEGATIVE_INFINITY, Long.MAX_VALUE), is(0.4));
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
}
