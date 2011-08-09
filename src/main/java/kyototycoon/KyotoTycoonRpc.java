package kyototycoon;

import kyototycoon.transcoder.Transcoder;

import java.util.List;
import java.util.Map;

public interface KyotoTycoonRpc {
    void setKeyTranscoder(Transcoder transcoder);
	void setValueTranscoder(Transcoder transcoder);

    // Common arguments
    void setTarget(String expression);

    //procedures
    Map<String,String> report();
    Map<String,String> status();
    void clear();
    void synchronize(boolean hard);
    void synchronize(boolean hard, String command);
    void set(Object key, Object value, ExpirationTime xt);
    void set(Object key, Object value);
    void add(Object key, Object value, ExpirationTime xt);
    void add(Object key, Object value);
    void replace(Object key, Object value, ExpirationTime xt);
    void replace(Object key, Object value);
    void append(Object key, Object value, ExpirationTime xt);
    void append(Object key, Object value);
    long increment(Object key, long num);
    long increment(Object key, long num, IncrementOrigin orig, ExpirationTime xt);
    double incrementDouble(Object key, double num);
    double incrementDouble(Object key, double num, IncrementOrigin orig, ExpirationTime xt);
    boolean cas(Object key, Object oval, Object nval);
    boolean cas(Object key, Object oval, Object nval, ExpirationTime xt);
    boolean remove(Object key);
    Object get(Object key);
    //TODO: How to return xt of a record?
    Object seize(Object key);
    long setBulk(Map<Object, Object> entries);
    long setBulk(Map<Object, Object> entries, ExpirationTime xt, boolean atomic);
    long removeBulk(List<Object> keys);
    long removeBulk(List<Object> keys, boolean atomic);
    Map<Object, Object> getBulk(List<Object> keys);
    Map<Object, Object> getBulk(List<Object> keys, boolean atomic);
    void vacuum();
    void vacuum(long step);
    List<Object> matchPrefix(Object prefix);
    List<Object> matchPrefix(Object prefix, long max);
    List<Object> matchRegex(Object regex);
    List<Object> matchRegex(Object regex, long max);
}
