package kyototycoon;

import com.google.common.collect.ImmutableMap;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RawTsvEncoding {
    public byte[] encode(Map<String, String> input) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            TsvWriter writer = new TsvWriter(buffer);
            for (Map.Entry<String, String> each : input.entrySet()) {
                writer.writeKey(each.getKey());
                writer.writeTab();
                writer.writeValue(each.getValue());
                writer.writeEol();
            }
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error while encode " + input);
        }
    }

    public Map<String, String> decode(byte[] input) {
        try {
            ImmutableMap.Builder<String, String> result = new ImmutableMap.Builder<String, String>();
            TsvReader reader = new TsvReader(input);
            while (reader.hasRemaining()) {
                String key = reader.readKey();
                reader.readTab();
                String value = reader.readValue();
                reader.readEol();
                result.put(key, value);
            }
            return result.build();
        } catch (Exception e) {
            throw new RuntimeException("Error while decode " + input);
        }
    }
}
