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
    private KyotoTycoonClient dut;

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

    @Test(expected=RuntimeException.class) public void incrementStringValue() {
        dut.set("count", "3");
        assertThat(dut.increment("count", 4L), is(7L));
    }

    @Test public void incrementDoubleNotExistingRecord() {
        assertThat(dut.incrementDouble("count", 0.1D), is(0.1D));
    }

    @Test(expected=RuntimeException.class) public void incrementDoubleStringValue() {
        dut.set("count", "0.3");
        assertThat(dut.incrementDouble("count", 0.4D), is(0.7D));
    }
}
