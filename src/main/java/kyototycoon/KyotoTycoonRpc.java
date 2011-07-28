package kyototycoon;

import kyototycoon.transcoder.Transcoder;

import java.util.Map;

public interface KyotoTycoonRpc {
    void setValueTranscoder(Transcoder transcoder);

    // Common arguments
    //DB

    Map<String,String> report();
    Map<String,String> status();
    void clear();
    void synchronize(boolean hard);
    void synchronize(boolean hard, String command);

    void set(String key, Object value, long xt);
    void set(String key, Object value);
    void add(String key, Object value, long xt);
    void add(String key, Object value);
    void replace(String key, Object value, long xt);
    void replace(String key, Object value);
    void append(String key, Object value, long xt);
    void append(String key, Object value);

    Object get(String key);
    long increment(String key, long num);
    long increment(String key, long num, long orig, long xt);
    double incrementDouble(String key, double num);
    double incrementDouble(String key, double num, double orig, long xt);
}
