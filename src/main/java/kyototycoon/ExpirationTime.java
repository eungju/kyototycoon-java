package kyototycoon;

public class ExpirationTime {
	/**
	 * in seconds.
	 */
	private final long value;

	private ExpirationTime(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}

	public boolean isEnabled() {
		return this != NONE;
	}

    public String toString() {
        if (isEnabled()) {
            return value < 0 ? "At " + Math.abs(value) : "After " + value;
        } else {
            return "NONE";
        }
    }
	
	public static ExpirationTime after(long ttl) {
		return new ExpirationTime(ttl);
	}
	
	public static ExpirationTime at(long epoch) {
		return new ExpirationTime(-epoch);
	}
	
	public static final ExpirationTime NONE = new ExpirationTime(Long.MAX_VALUE);
}