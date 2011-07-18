package kyototycoon.tsvrpc;

import java.io.IOException;
import java.io.OutputStream;

public class TsvWriter {
    private OutputStream output;

    public TsvWriter(OutputStream output) {
        this.output = output;
    }

    public void writeKey(byte[] key) throws IOException {
        output.write(key);
    }

    public void writeTab() throws IOException {
        output.write('\t');
    }

    public void writeValue(byte[] value) throws IOException {
        output.write(value);
    }
    
    public void writeEol() throws IOException {
        output.write('\r');
        output.write('\n');
    }
}
