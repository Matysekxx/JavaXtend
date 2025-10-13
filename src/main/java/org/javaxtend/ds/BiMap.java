package org.javaxtend.ds;

import org.javaxtend.validation.Guard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

/**
 * A bidirectional map that maintains a one-to-one correspondence between keys and values.
 * <p>
 * This map enforces uniqueness for both keys and values. Attempting to insert a key-value pair
 * where the value is already present with a different key will result in the removal of the old entry.
 * Similarly, inserting a pair with an existing key will update its value.
 * <p>
 * This implementation is not thread-safe. Null keys and values are not supported.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * BiMap<String, String> countryCodes = new BiMap<>();
 * countryCodes.put("US", "United States");
 * countryCodes.put("CZ", "Czech Republic");
 *
 * String countryName = countryCodes.get("US"); // "United States"
 * String countryCode = countryCodes.inverse().get("Czech Republic"); // "CZ"
 * }</pre></blockquote>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class BiMap<K, V> {
    private final Map<K, V> forwardMap;
    private final Map<V, K> backwardMap;

    private BiMap<V, K> inverse;

    /**
     * Returns an inverse view of this bimap, where the values are keys and the keys are values.
     * <p>
     * Changes to the inverse bimap will be reflected in the original bimap, and vice versa.
     *
     * @return an inverse view of this bimap.
     */
    public BiMap<V,K> inverse() {
        if (inverse == null) {
            inverse = new BiMap<>(backwardMap, forwardMap,this);
        }
        return inverse;
    }

    public BiMap() {
        forwardMap = new HashMap<>();
        backwardMap = new HashMap<>();
    }
    private BiMap(
            Map<K, V> forwardMap,
            Map<V, K> backwardMap,
            BiMap<V, K> inverse) {
        this.forwardMap = forwardMap;
        this.backwardMap = backwardMap;
        this.inverse = inverse;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * <p>
     * If the map previously contained a mapping for the key, the old value is replaced.
     * If the map previously contained a mapping for the value, the old key is removed.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping for the key.
     */
    public V put(K key, V value) {
        Guard.against().isNull(key, "Key cannot be null");
        Guard.against().isNull(value, "Value cannot be null");
        if (backwardMap.containsKey(value)) {
            K existingKey = backwardMap.get(value);
            if (!Objects.equals(existingKey, key)) {
                forwardMap.remove(existingKey);
            }
        }
        final V oldValue = forwardMap.put(key, value);
        if (oldValue != null) {
            backwardMap.remove(oldValue);
        }
        backwardMap.put(value, key);

        return oldValue;
    }

    /**
     * Removes the mapping for a key from this bimap if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or {@code null} if there was no mapping for key.
     */
    public V remove(Object key) {
        V value = forwardMap.remove(key);
        if (value != null) {
            backwardMap.remove(value);
        }
        return value;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    public boolean containsKey(Object key){
        return forwardMap.containsKey(key);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value.
     * This operation is fast (O(1)) due to the internal backward map.
     *
     * @param value The value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    public boolean containsValue(Object value) {
        return backwardMap.containsKey(value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null}
     */
    public V get(Object key){
        return forwardMap.get(key);
    }

    /**
     * Returns the key to which the specified value is mapped,
     * or {@code null} if this map contains no mapping for the value.
     * This operation is fast (O(1)).
     *
     * @param value the value whose associated key is to be returned
     * @return the key to which the specified value is mapped, or {@code null}
     */
    public K getKey(Object value) {
        return backwardMap.get(value);
    }

    /**
     * Returns an unmodifiable {@link Set} view of the keys contained in this map.
     *
     * @return an unmodifiable set view of the keys contained in this map
     */
    public Set<K> keySet() {
        return Collections.unmodifiableSet(forwardMap.keySet());
    }

    /**
     * Returns an unmodifiable {@link Set} view of the values contained in this map.
     *
     * @return an unmodifiable set view of the values contained in this map
     */
    public Set<V> values() {
        return Collections.unmodifiableSet(backwardMap.keySet());
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return forwardMap.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return forwardMap.isEmpty();
    }

    /**
     * Removes all the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        forwardMap.clear();
        backwardMap.clear();
    }

    @Override
    public String toString() {
        return forwardMap.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiMap<?, ?> biMap = (BiMap<?, ?>) o;
        return Objects.equals(forwardMap, biMap.forwardMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(forwardMap);
    }
}
