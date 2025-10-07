package org.javaxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmutablePairTest {

    @Test
    @DisplayName("Should create a tuple and get its elements")
    void testCreationAndGetters() {
        Pair<String, Integer> pair = ImmutablePair.of("Test", 123);

        assertEquals("Test", pair.first());
        assertEquals(123, pair.second());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        Pair<String, Integer> pair1 = ImmutablePair.of("A", 1);
        Pair<String, Integer> pair2 = ImmutablePair.of("A", 1);
        Pair<String, Integer> pair3 = ImmutablePair.of("B", 2);

        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);

        assertEquals(pair1.hashCode(), pair2.hashCode());
        assertNotEquals(pair1.hashCode(), pair3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        Pair<String, Integer> pair = ImmutablePair.of(null, null);

        assertNull(pair.first());
        assertNull(pair.second());
        assertTrue(pair.isEmpty());
        assertTrue(pair.contains(null));
    }

    @Test
    @DisplayName("Should produce correct string representation")
    void testToString() {
        Pair<String, Integer> pair = ImmutablePair.of("Hello", 2024);
        assertEquals("(Hello, 2024)", pair.toString());
    }

    @Test
    @DisplayName("Should swap elements correctly")
    void testSwap() {
        ImmutablePair<String, Integer> original = ImmutablePair.of("One", 2);
        ImmutablePair<Integer, String> swapped = original.swap();

        assertEquals(2, swapped.first());
        assertEquals("One", swapped.second());
    }

    @Test
    @DisplayName("Should map elements correctly")
    void testMap() {
        ImmutablePair<String, Integer> original = ImmutablePair.of("5", 10);
        ImmutablePair<Integer, String> mapped = original.map(Integer::parseInt, Object::toString);

        assertEquals(5, mapped.first());
        assertEquals("10", mapped.second());
    }

    @Test
    @DisplayName("Should map first element only")
    void testMapFirst() {
        ImmutablePair<String, Integer> original = ImmutablePair.of("Hello", 10);
        ImmutablePair<Integer, Integer> mapped = original.mapFirst(String::length);

        assertEquals(5, mapped.first());
        assertEquals(10, mapped.second());
    }

    @Test
    @DisplayName("Should convert to a mutable tuple")
    void testToMutable() {
        ImmutablePair<String, Integer> immutable = ImmutablePair.of("A", 1);
        JXPair<String, Integer> mutable = immutable.toMutable();

        assertEquals(immutable.first(), mutable.first());
        assertEquals(immutable.second(), mutable.second());
        assertInstanceOf(JXPair.class, mutable);
    }
}