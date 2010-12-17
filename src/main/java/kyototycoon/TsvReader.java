package kyototycoon;

public class TsvReader {
    private final byte[] input;
    private int pos;
    
    public TsvReader(byte[] input) {
        this.input = input;
        pos = 0;
    }

    public boolean hasRemaining() {
        return pos < input.length;
    }

    public String readKey() {
        int s = pos;
        while (input[pos] != '\t') {
            pos++;
        }
        return new String(input, s, pos - s);
    }

    public void readTab() {
        pos++;
    }

    public String readValue() {
        int s = pos;
        while (pos < input.length && !isEolChar(input[pos])) {
            pos++;
        }
        return new String(input, s, pos - s);
    }

    public void readEol() {
        while (pos < input.length && isEolChar(input[pos])) {
            pos++;
        }
    }

    boolean isEolChar(byte c) {
        return c == '\r' || c == '\n';
    }
}
