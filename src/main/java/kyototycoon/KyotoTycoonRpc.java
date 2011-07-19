package kyototycoon;

import kyototycoon.transcoder.Transcoder;

public interface KyotoTycoonRpc {
    void setKeyTranscoder(Transcoder transcoder);
    void setValueTranscoder(Transcoder transcoder);

    // Common arguments
    //DB

    void set(Object key, Object value);
    Object get(Object key);
    void clear();
    long increment(Object key, long num);
    double incrementDouble(Object key, double num);
}
