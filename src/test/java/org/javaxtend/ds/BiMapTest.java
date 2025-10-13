package org.javaxtend.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BiMapTest {

    private BiMap<String, Integer> biMap;

    @BeforeEach
    void setUp() {
        biMap = new BiMap<>();
    }

    @Test
    @DisplayName("put() should insert a new key-value pair")
    void put_insertsNewPair() {
        biMap.put("A", 1);
        assertEquals(1, biMap.size());
        assertEquals(1, biMap.get("A"));
        assertEquals("A", biMap.getKey(1));
    }

    @Test
    @DisplayName("put() should overwrite existing key")
    void put_overwritesExistingKey() {
        biMap.put("A", 1);
        biMap.put("A", 2);

        assertEquals(1, biMap.size());
        assertEquals(2, biMap.get("A"));
        assertNull(biMap.getKey(1), "Old value should be gone");
        assertEquals("A", biMap.getKey(2));
    }

    @Test
    @DisplayName("put() should remove old key when value is duplicated")
    void put_removesOldKeyForDuplicateValue() {
        biMap.put("A", 1);
        biMap.put("B", 1);

        assertEquals(1, biMap.size());
        assertFalse(biMap.containsKey("A"), "Old key 'A' should be removed");
        assertTrue(biMap.containsKey("B"));
        assertEquals(1, biMap.get("B"));
        assertEquals("B", biMap.getKey(1));
    }

    @Test
    @DisplayName("get() and getKey() should retrieve correct entries")
    void getAndGetKey_retrievesCorrectly() {
        biMap.put("A", 1);
        biMap.put("B", 2);

        assertEquals(1, biMap.get("A"));
        assertEquals("B", biMap.getKey(2));
        assertNull(biMap.get("C"));
        assertNull(biMap.getKey(3));
    }

    @Test
    @DisplayName("containsKey() and containsValue() should work correctly")
    void containsKeyAndValue_workCorrectly() {
        biMap.put("A", 1);

        assertTrue(biMap.containsKey("A"));
        assertFalse(biMap.containsKey("B"));

        assertTrue(biMap.containsValue(1));
        assertFalse(biMap.containsValue(2));
    }

    @Test
    @DisplayName("size() and isEmpty() should reflect the map state")
    void sizeAndIsEmpty_reflectState() {
        assertTrue(biMap.isEmpty());
        assertEquals(0, biMap.size());

        biMap.put("A", 1);

        assertFalse(biMap.isEmpty());
        assertEquals(1, biMap.size());
    }

    @Test
    @DisplayName("clear() should remove all entries")
    void clear_removesAllEntries() {
        biMap.put("A", 1);
        biMap.put("B", 2);

        biMap.clear();

        assertTrue(biMap.isEmpty());
        assertEquals(0, biMap.size());
        assertNull(biMap.get("A"));
        assertNull(biMap.getKey(2));
    }

    @Test
    @DisplayName("keySet() and values() should return correct unmodifiable sets")
    void keySetAndValues_returnCorrectSets() {
        biMap.put("A", 1);
        biMap.put("B", 2);

        Set<String> keys = biMap.keySet();
        Set<Integer> values = biMap.values();

        assertEquals(Set.of("A", "B"), keys);
        assertEquals(Set.of(1, 2), values);

        assertThrows(UnsupportedOperationException.class, () -> keys.add("C"));
        assertThrows(UnsupportedOperationException.class, () -> values.add(3));
    }

    @Test
    @DisplayName("inverse() should provide a working inverse view")
    void inverse_providesWorkingInverseView() {
        biMap.put("A", 1);
        biMap.put("B", 2);

        BiMap<Integer, String> inverseMap = biMap.inverse();

        assertEquals("A", inverseMap.get(1));
        assertEquals("B", inverseMap.get(2));
        assertTrue(inverseMap.containsKey(1));
        assertTrue(inverseMap.containsValue("B"));
    }

    @Test
    @DisplayName("Changes in inverse view should reflect in original map")
    void inverse_changesReflectInOriginal() {
        biMap.put("A", 1);
        BiMap<Integer, String> inverseMap = biMap.inverse();

        inverseMap.put(3, "C");

        assertTrue(biMap.containsKey("C"));
        assertEquals(3, biMap.get("C"));
        assertEquals(2, biMap.size());
    }

    @Test
    @DisplayName("remove() should remove the entry from both maps")
    void remove_removesEntryCorrectly() {
        biMap.put("A", 1);
        biMap.put("B", 2);

        Integer removedValue = biMap.remove("A");

        assertEquals(1, removedValue);
        assertEquals(1, biMap.size());
        assertFalse(biMap.containsKey("A"));
        assertNull(biMap.get("A"));
        assertFalse(biMap.containsValue(1));
        assertNull(biMap.getKey(1));

        assertTrue(biMap.containsKey("B"));
        assertEquals(2, biMap.get("B"));
    }

    @Test
    @DisplayName("remove() on non-existent key should return null")
    void remove_nonExistentKey() {
        biMap.put("A", 1);
        assertNull(biMap.remove("Z"));
        assertEquals(1, biMap.size());
    }
}