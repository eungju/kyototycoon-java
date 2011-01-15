package kyototycoon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Values implements Iterable<Map.Entry<String, String>> {
    private final Map<String, String> container = new HashMap<String, String>();

    public Values put(String key, String value) {
        container.put(key, value);
        return this;
    }
    
    public String get(String key) {
        return container.get(key);
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        return container.entrySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Values values = (Values) o;

        if (!container.equals(values.container)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return container.hashCode();
    }
}
