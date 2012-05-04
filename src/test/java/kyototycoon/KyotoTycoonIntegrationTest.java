package kyototycoon;

import com.google.common.base.Strings;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Run <code>ktserver '+' '-'</code>
 */
public class KyotoTycoonIntegrationTest {
    private SimpleKyotoTycoonClient dut;
    private KyotoTycoonConnection conn;

    @Before
    public void beforeEach() throws Exception {
        dut = new SimpleKyotoTycoonClient();
        dut.setHost(KyotoTycoonFixture.SERVER_ADDRESS);
        dut.start();
        dut.clear();
        conn = dut.getConnection();
    }

    @After
    public void afterEach() {
        conn.close();
        dut.stop();
    }

    @Test public void
    report_returns_the_report_of_the_server_information() {
        Map<String, String> actual = dut.report();
        System.out.println(actual);
        assertThat(actual, Matchers.<Object>notNullValue());
    }

    @Ignore
    @Test public void
    play_script_calls_a_procedure_of_the_scripting_extension() {
        Map<Object, Object> records = new HashMap<Object, Object>();
        List<Object> result = dut.playScript("name", records);
    }

    @Ignore
    @Test public void
    tune_replication_set_the_replication_configuration() {
        dut.tuneReplication();
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

    @Test public void
    get_bulk_retrieves_very_large_number_of_records_at_once() {
        List<Object> keys = new ArrayList<Object>();
        String value = Strings.repeat("0", 1024);
        for (int i = 0; i < 1024; i++) {
            Object key = String.valueOf(i);
            keys.add(key);
            dut.set(key, value);
        }
        assertThat(dut.getBulk(keys).size(), is(keys.size()));
    }

    @Test public void
    vacuum_scans_the_database_and_eliminate_regions_of_expired_records() throws InterruptedException {
        dut.set("key", "value", ExpirationTime.after(1));
        dut.vacuum();
        assertThat(dut.status().get("count"), is("1"));
    }

    @Test public void
    match_prefix_returns_keys_matching_a_prefix_string() {
        dut.set("a", "1");
        dut.set("aa", "11");
        dut.set("b", "2");
        assertThat(dut.matchPrefix("a"), is(Arrays.<Object>asList("a", "aa")));
    }

    @Test public void
    match_regex_returns_keys_matching_a_regular_expression_string() {
        dut.set("a", "1");
        dut.set("aa", "11");
        dut.set("b", "2");
        dut.set("ba", "21");
        assertThat(dut.matchRegex(".a"), is(Arrays.<Object>asList("aa", "ba")));
    }

    @Test public void
    specifies_the_identifier_of_the_target_database_of_each_operation() {
        dut.set("key", "value");
        dut.setTarget("-");
        assertThat(dut.get("key"), nullValue());
    }

    @Test public void
    cursor_jumps_to_the_first_record_for_forward_scan() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        assertThat(c.jump(), is(true));
        assertThat((String) c.getKey(), is("a"));
        c.close();
    }

    @Test public void
    cursor_can_not_jump_to_a_non_existing_record() {
        Cursor c = conn.cursor();
        assertThat(c.jump(), is(false));
        c.close();
    }

    @Test public void
    cursor_jumps_to_the_last_record_for_backward_scan() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        assertThat(c.jumpBack(), is(true));
        assertThat((String) c.getKey(), is("b"));
        c.close();
    }

    @Test public void
    cursor_can_not_jump_back_to_a_non_existing_record() {
        Cursor c = conn.cursor();
        assertThat(c.jumpBack(), is(false));
        c.close();
    }

    @Test public void
    cursor_steps_to_the_next_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat(c.step(), is(true));
        assertThat((String) c.getKey(), is("b"));
        assertThat(c.step(), is(false));
        c.close();
    }

    @Test public void
    cursor_steps_back_to_the_previous_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jumpBack();
        assertThat(c.stepBack(), is(true));
        assertThat((String) c.getKey(), is("a"));
        assertThat(c.stepBack(), is(false));
        c.close();
    }

    @Test public void
    cursor_sets_the_value_of_the_current_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat(c.setValue("3"), is(true));
        assertThat((String) c.getValue(), is("3"));
        assertThat((String) c.getKey(), is("a"));
        assertThat(c.setValue("4", ExpirationTime.NONE, true), is(true));
        assertThat((String) c.getKey(), is("b"));
        assertThat(c.setValue("5", ExpirationTime.NONE, true), is(true));
        assertThat(c.setValue("6"), is(false));
        c.close();
    }

    @Test public void
    cursor_remove_the_current_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat(c.remove(), is(true));
        assertThat((String) c.getKey(), is("b"));
        assertThat(c.remove(), is(true));
        assertThat(c.remove(), is(false));
        c.close();
    }

    @Test public void
    cursor_gets_the_key_of_the_current_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat((String) c.getKey(), is("a"));
        assertThat((String) c.getKey(true), is("a"));
        assertThat((String) c.getKey(true), is("b"));
        assertThat(c.getKey(true), nullValue());
        c.close();
    }

    @Test public void
    cursor_gets_the_value_of_the_current_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat((String) c.getValue(), is("1"));
        assertThat((String)c.getValue(true), is("1"));
        assertThat((String)c.getValue(true), is("2"));
        assertThat(c.getValue(true), nullValue());
        c.close();
    }

    @Test public void
    cursor_gets_a_pair_of_the_key_and_the_value_of_the_current_record() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat(c.get(), is(new Record("a", "1")));
        assertThat(c.get(true), is(new Record("a", "1")));
        assertThat(c.get(true), is(new Record("b", "2")));
        assertThat(c.get(true), nullValue());
        c.close();
    }

    @Test public void
    cursor_gets_a_pair_of_the_key_and_the_value_of_the_current_record_and_remove_it_atomically() {
        dut.set("a", "1");
        dut.set("b", "2");
        Cursor c = conn.cursor();
        c.jump();
        assertThat(c.seize(), is(new Record("a", "1")));
        assertThat(dut.get("a"), nullValue());
        assertThat(c.seize(), is(new Record("b", "2")));
        assertThat(dut.get("a"), nullValue());
        assertThat(c.seize(), nullValue());
        c.close();
    }

    @Test public void
    waiting_and_signaling_on_conditional_variables() throws InterruptedException {
        Thread waitingThread = new Thread(new Runnable() {
            public void run() {
                KyotoTycoonConnection conn = dut.getConnection();
                try {
                    conn.setSignalWaiting("ping");
                    assertThat((String) conn.get("key"), is("value"));
                } finally {
                    conn.close();
                }
            }
        });
        waitingThread.start();
        Thread.sleep(100);
        
        KyotoTycoonConnection conn = dut.getConnection();
        try {
            conn.setSignalSending("ping");
            conn.set("key", "value");
        } finally {
            conn.close();
        }

        waitingThread.join();
    }
}
