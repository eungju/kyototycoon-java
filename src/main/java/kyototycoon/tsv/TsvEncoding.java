package kyototycoon.tsv;

import java.io.ByteArrayOutputStream;

public class TsvEncoding {
    public final String contentType;
    public final ValueEncoding valueEncoding;

    public TsvEncoding(String contentType, ValueEncoding valueEncoding) {
        this.contentType = contentType;
        this.valueEncoding = valueEncoding;
    }

    public byte[] encode(Values input) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            TsvWriter writer = new TsvWriter(buffer);
            for (KeyValuePair each : input) {
                writer.writeKey(valueEncoding.encode(each.key));
                writer.writeTab();
                writer.writeValue(valueEncoding.encode(each.value));
                writer.writeEol();
            }
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error while encode " + input, e);
        }
    }

    public Values decode(byte[] input) {
        try {
            Values result = new Values();
            TsvReader reader = new TsvReader(input);
            while (reader.hasRemaining()) {
                byte[] key = valueEncoding.decode(reader.readKey());
                reader.readTab();
                byte[] value = valueEncoding.decode(reader.readValue());
                reader.readEol();
                result.put(key, value);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error while decode " + input, e);
        }
    }
}
