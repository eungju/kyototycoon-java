package kyototycoon;

import java.io.IOException;
import java.io.OutputStream;

public class TsvWriter {
    private OutputStream output;

    public TsvWriter(OutputStream output) {
        this.output = output;
    }

    public void writeKey(String key) throws IOException {
        output.write(key.getBytes());
    }

    public void writeTab() throws IOException {
        output.write('\t');
    }

    public void writeValue(String value) throws IOException {
        output.write(value.getBytes());
    }
    
    public void writeEol() throws IOException {
        output.write('\r');
        output.write('\n');
    }
}
