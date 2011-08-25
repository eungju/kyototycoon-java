package kyototycoon;

public class Record {
    public final Object key;
    public final Object value;
    public final ExpirationTime xt;

    public Record(Object key, Object value) {
        this(key, value, ExpirationTime.NONE);
    }
    
    public Record(Object key, Object value, ExpirationTime xt) {
        this.key = key;
        this.value = value;
        this.xt = xt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (!key.equals(record.key)) return false;
        if (!value.equals(record.value)) return false;
        if (!xt.equals(record.xt)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + xt.hashCode();
        return result;
    }

    public String toString() {
        return "Record(" + key + "," + value + "," + xt + ")";
    }
}
