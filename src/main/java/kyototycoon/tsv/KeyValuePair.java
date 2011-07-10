package kyototycoon.tsv;

import java.util.Arrays;

public class KeyValuePair {
    public final byte[] key;
    public final byte[] value;

    public KeyValuePair(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public boolean equals(Object o) {
        KeyValuePair other = (KeyValuePair) o;
        return Arrays.equals(key, other.key) && Arrays.equals(value, other.value);
    }
}
