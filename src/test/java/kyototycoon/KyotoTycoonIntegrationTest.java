package kyototycoon;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    @Test public void
    report_returns_the_report_of_the_server_information() {
        Map<String, String> actual = dut.report();
        System.out.println(actual);
        assertThat(actual, Matchers.<Object>notNullValue());
    }

    @Test public void
    status_returns_the_miscellaneous_status_information_of_a_database() {
        Map<String, String> actual = dut.status();
        System.out.println(actual);
        assertThat(actual, Matchers.<Object>notNullValue());
    }

    @Test public void
    clear_removes_all_records_in_a_database() {
        dut.set("key", "value");
        dut.clear();
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    synchronize_synchronizes_updated_contents_with_the_file_and_the_device() {
        dut.synchronize(true);
    }

    @Test public void
    set_sets_the_value_of_a_record() {
        dut.set("key", "value");
        assertThat((String) dut.get("key"), is("value"));
        dut.set("key", "other");
        assertThat((String) dut.get("key"), is("other"));
    }

    @Test public void
    add_sets_the_value_of_a_record_if_record_does_not_exist() {
        dut.add("key", "value");
        assertThat((String) dut.get("key"), is("value"));
    }

    @Test(expected=RuntimeException.class) public void
    add_throws_exception_if_a_record_exist() {
        dut.set("key", "value");
        dut.add("key", "value");
    }

    @Test public void
    replace_sets_the_value_of_a_record_if_a_record_exist() {
        dut.set("key", "1");
        dut.replace("key", "2");
        assertThat((String) dut.get("key"), is("2"));
    }

    @Test(expected=RuntimeException.class) public void
    replace_throws_exception_if_record_does_not_exist() {
        dut.replace("key", "value");
    }

    @Test public void
    append_appends_the_value_to_the_value_of_a_record() {
        dut.add("key", "1");
        dut.append("key", "23");
        assertThat((String) dut.get("key"), is("123"));
    }

    @Test public void
    append_sets_the_value_of_a_record_if_record_does_not_exist() {
        dut.append("key", "23");
        assertThat((String) dut.get("key"), is("23"));
    }
    
    @Test public void
    increment_adds_the_number_to_the_value_of_a_record() {
    	dut.increment("key", 3L);
    	assertThat(dut.increment("key", 4), is(7L));
    }

    @Test public void
    increment_sets_the_number_if_record_does_not_exist() {
    	assertThat(dut.increment("key", 3), is(3L));
    }

    @Test(expected=RuntimeException.class) public void
    increment_throws_exception_if_the_value_of_a_record_is_not_a_numeric_integer() {
        dut.set("key", "3");
        dut.increment("key", 4L);
    }

    @Test public void
    increment_sets_the_value_of_a_record_if_the_origin_is_set() {
        dut.increment("key", 3L);
        assertThat(dut.increment("key", 4L, IncrementOrigin.SET, ExpirationTime.NONE), is(4L));
    }

    @Test(expected=RuntimeException.class) public void
    increment_throws_exception_if_the_origin_is_try_and_the_value_of_a_record_is_not_a_numeric_integer() {
        dut.increment("key", 4L, IncrementOrigin.TRY, ExpirationTime.NONE);
    }

    @Test public void
    increment_double_adds_the_number_to_the_value_of_a_record() {
    	dut.incrementDouble("key", 0.3);
    	assertThat(dut.incrementDouble("key", 0.4), is(0.7));
    }

    @Test public void
    increment_double_sets_the_number_if_record_does_not_exist() {
    	assertThat(dut.incrementDouble("key", 0.3), is(0.3));
    }

    @Test(expected=RuntimeException.class) public void
    increment_double_throws_exception_if_the_value_of_a_record_is_not_a_numeric_double() {
        dut.set("key", "0.3");
        dut.incrementDouble("key", 0.4);
    }

    @Test public void
    increment_double_sets_the_value_of_a_record_if_the_origin_is_set() {
        dut.incrementDouble("key", 0.3);
        assertThat(dut.incrementDouble("key", 0.4, IncrementOrigin.SET, ExpirationTime.NONE), is(0.4));
    }

    @Test(expected=RuntimeException.class) public void
    increment_double_throws_exception_if_the_origin_is_try_and_the_value_of_a_record_is_not_a_numeric_double() {
        dut.incrementDouble("key", 0.4, IncrementOrigin.TRY, ExpirationTime.NONE);
    }

    @Test public void
    cas_sets_the_value_of_a_record_if_the_old_value_matches() {
        dut.set("key", "1");
        assertThat(dut.cas("key", "1", "2"), is(true));
        assertThat((String) dut.get("key"), is("2"));
    }

    @Test public void
    cas_does_not_set_the_value_of_a_record_if_the_old_value_does_not_match() {
        dut.set("key", "1");
        assertThat(dut.cas("key", "2", "3"), is(false));
        assertThat((String) dut.get("key"), is("1"));
    }

    @Test public void
    cas_removes_a_record_if_the_new_value_is_null() {
        dut.set("key", "1");
        assertThat(dut.cas("key", "1", null), is(true));
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    remove_removes_a_record() {
        dut.set("key", "value");
        assertThat(dut.remove("key"), is(true));
    }

    @Test public void
    remove_returns_false_if_record_does_not_exist() {
        assertThat(dut.remove("key"), is(false));
    }

    @Test public void
    get_retrieves_the_value_of_a_record() {
        dut.set("key", "value");
        assertThat((String) dut.get("key"), is("value"));
    }

    @Test public void
    get_returns_null_if_record_does_not_exist() {
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    seize_retrieves_the_value_of_a_record_and_remove_it_atomically() {
        dut.set("key", "value");
        assertThat((String) dut.seize("key"), is("value"));
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    seize_returns_null_if_record_does_not_exist() {
        assertThat((String) dut.seize("key"), nullValue());
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    set_bulk_stores_records_at_once() {
        Map<Object, Object> entries = new HashMap<Object, Object>();
        entries.put("a", "1");
        entries.put("b", "2");
        assertThat(dut.setBulk(entries), is((long) entries.size()));
        assertThat((String) dut.get("a"), is("1"));
        assertThat((String) dut.get("b"), is("2"));
    }

    @Test public void
    remove_bulk_removes_records_at_once() {
        dut.set("a", "1");
        List<Object> keys = Arrays.<Object>asList("a", "b");
        assertThat(dut.removeBulk(keys), is((long) keys.size() - 1));
        assertThat((String) dut.get("a"), nullValue());
        assertThat((String) dut.get("b"), nullValue());
    }

    @Test public void
    get_bulk_retrieves_records_at_once() {
        dut.set("a", "1");
        List<Object> keys = Arrays.<Object>asList("a", "b");
        Map<Object, Object> expected = new HashMap<Object, Object>();
        expected.put("a", "1");
        assertThat(dut.getBulk(keys), is(expected));
    }
}
