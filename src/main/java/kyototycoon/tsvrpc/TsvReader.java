package kyototycoon.tsvrpc;

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

    public byte[] readKey() {
        int s = pos;
        while (pos < input.length) {
            byte c = input[pos];
            if (c == '\t' || c == '\r' || c == '\n') {
                break;
            }
            pos++;
        }
        byte[] buffer = new byte[pos - s];
        System.arraycopy(input, s, buffer, 0, buffer.length);
        return buffer;
    }

    public void readTab() {
        while (pos < input.length && input[pos] == '\t') {
            pos++;
        }
    }

    public byte[] readValue() {
        int s = pos;
        while (pos < input.length && !isEolChar(input[pos])) {
            pos++;
        }
        byte[] buffer = new byte[pos - s];
        System.arraycopy(input, s, buffer, 0, buffer.length);
        return buffer;
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
