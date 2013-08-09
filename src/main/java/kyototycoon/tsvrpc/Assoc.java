package kyototycoon.tsvrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Assoc implements Iterable<KeyValuePair> {
    private final List<KeyValuePair> entries = new ArrayList<KeyValuePair>();

    private static final byte[] NONE = new byte[0];
    public Assoc put(byte[] key) {
        return put(key, NONE);
    }

    public Assoc put(byte[] key, byte[] value) {
        entries.add(new KeyValuePair(key, value));
        return this;
    }

    public byte[] get(byte[] key) {
        for (KeyValuePair entry : entries) {
            if (Arrays.equals(key, entry.key)) {
                return entry.value;
            }
        }
        return null;
    }

    public Iterator<KeyValuePair> iterator() {
        return entries.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assoc assoc = (Assoc) o;
        for (KeyValuePair entry : entries) {
            if (!Arrays.equals(get(entry.key), (assoc.get(entry.key)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    @Override
    public String toString() {
    	StringBuilder buffer = new StringBuilder("[");
    	boolean first = true;
    	for (KeyValuePair entry : entries) {
    		if (first) {
    			first = false;
    		} else {
    			buffer.append(", ");
    		}
    		buffer.append(entry.toString());
    	}
    	buffer.append("]");
        return buffer.toString();
    }
}
