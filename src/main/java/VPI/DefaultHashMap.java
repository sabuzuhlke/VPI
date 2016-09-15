package VPI;

import java.util.HashMap;

/**
 * This class allows to return a default value when queried with an unrecognised key
 * @param <K>
 * @param <V>
 */
public class DefaultHashMap<K,V> extends HashMap<K,V> {
    protected V defaultValue;
    public DefaultHashMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }
    @Override
    public V get(Object k) {
        return containsKey(k) ? super.get(k) : defaultValue;
    }
}