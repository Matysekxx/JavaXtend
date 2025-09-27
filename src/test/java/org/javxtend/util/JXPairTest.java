package org.javxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JXPairTest {

    @Test
    @DisplayName("Should create a tuple and get its elements")
    void testCreationAndGetters() {
        Pair<String, Integer> pair = JXPair.of("Test", 123);
        assertEquals("Test", pair.getFirst());
        assertEquals(123, pair.getSecond());
    }

    @Test
    @DisplayName("Should modify elements using setters")
    void testSetters() {
        JXPair<String, Integer> tuple = JXPair.of("Initial", 1);
        tuple.setFirst("Modified");
        tuple.setSecond(2);

        assertEquals("Modified", tuple.getFirst());
        assertEquals(2, tuple.getSecond());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        Pair<String, Integer> pair1 = JXPair.of("A", 1);
        Pair<String, Integer> pair2 = JXPair.of("A", 1);
        Pair<String, Integer> pair3 = JXPair.of("B", 2);

        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);

        assertEquals(pair1.hashCode(), pair2.hashCode());
        assertNotEquals(pair1.hashCode(), pair3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        Pair<String, Integer> pair = JXPair.of(null, null);

        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
        assertTrue(pair.isEmpty());
        assertTrue(pair.contains(null));
    }

    @Test
    @DisplayName("Should produce correct string representation")
    void testToString() {
        Pair<String, Integer> pair = JXPair.of("Hello", 2024);
        assertEquals("(Hello, 2024)", pair.toString());
    }

    @Test
    @DisplayName("Should swap elements correctly")
    void testSwap() {
        JXPair<String, Integer> original = JXPair.of("One", 2);
        JXPair<Integer, String> swapped = original.swap();

        assertEquals(2, swapped.getFirst());
        assertEquals("One", swapped.getSecond());
    }

    @Test
    @DisplayName("Should map elements correctly")
    void testMap() {
        JXPair<String, Integer> original = JXPair.of("5", 10);
        JXPair<Integer, String> mapped = original.map(Integer::parseInt, Object::toString);

        assertEquals(5, mapped.getFirst());
        assertEquals("10", mapped.getSecond());
    }

    @Test
    @DisplayName("Should map second element only")
    void testMapSecond() {
        JXPair<String, Integer> original = JXPair.of("Hello", 10);
        JXPair<String, String> mapped = original.mapSecond(val -> "Value: " + val);

        assertEquals("Hello", mapped.getFirst());
        assertEquals("Value: 10", mapped.getSecond());
    }

    @Test
    @DisplayName("Should convert to an immutable tuple")
    void testToImmutable() {
        Pair<String, Integer> mutable = JXPair.of("A", 1);
        ImmutablePair<String, Integer> immutable = ((JXPair<String, Integer>) mutable).toImmutable();

        assertEquals(mutable.getFirst(), immutable.getFirst());
        assertEquals(mutable.getSecond(), immutable.getSecond());
        assertInstanceOf(ImmutablePair.class, immutable);
    }
}