package kyototycoon;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RawTsvEncoding {
    public byte[] encode(Map<String, String> input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (Map.Entry<String, String> each : input.entrySet()) {
            String key = each.getKey();
            String value = each.getValue();
            for (char c : key.toCharArray()) {
                buffer.write(c);
            }
            buffer.write('\t');
            for (char c : value.toCharArray()) {
                buffer.write(c);
            }
            buffer.write('\r');
            buffer.write('\n');
        }
        return buffer.toByteArray();
    }

    public Map<String, String> decode(byte[] input) {
        Map<String, String> result = new HashMap<String, String>();
        int i = 0;
        while (i < input.length) {
            //key
            int s = i;
            while (input[i] != '\t') {
                i++;
            }
            String key = new String(input, s, i - s);

            //tab
            i++;

            //value
            s = i;
            while (i < input.length && !isEolChar(input[i])) {
                i++;
            }
            String value = new String(input, s, i - s);

            //eol
            while (i < input.length && isEolChar(input[i])) {
                i++;
            }

            result.put(key, value);
        }
        return result;
    }

    boolean isEolChar(byte c) {
        return c == '\r' || c == '\n';
    }
}
