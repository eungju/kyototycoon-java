package kyototycoon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Values implements Iterable<Map.Entry<String, byte[]>> {
    private final Map<String, byte[]> container = new HashMap<String, byte[]>();

    public Values put(String key, byte[] value) {
        container.put(key, value);
        return this;
    }
    
    public byte[] get(String key) {
        return container.get(key);
    }

    public Iterator<Map.Entry<String, byte[]>> iterator() {
        return container.entrySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Values values = (Values) o;

        for (String each : container.keySet()) {
            if (!Arrays.equals(get(each), (values.get(each)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return container.hashCode();
    }
}
