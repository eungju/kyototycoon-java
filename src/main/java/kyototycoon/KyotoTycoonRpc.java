package kyototycoon;

import kyototycoon.transcoder.Transcoder;

import java.util.Map;

public interface KyotoTycoonRpc {
    void setKeyTranscoder(Transcoder transcoder);
    void setValueTranscoder(Transcoder transcoder);

    // Common arguments
    //DB

    Map<String,String> report();
    Map<String,String> status();

    void set(Object key, Object value);
    Object get(Object key);
    void clear();
    long increment(Object key, long num);
    double incrementDouble(Object key, double num);
}
