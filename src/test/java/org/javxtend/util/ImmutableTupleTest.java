package org.javxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableTupleTest {

    @Test
    @DisplayName("Should create a tuple and get its elements")
    void testCreationAndGetters() {
        Tuple<String, Integer> tuple = ImmutableTuple.of("Test", 123);

        assertEquals("Test", tuple.getFirst());
        assertEquals(123, tuple.getSecond());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        Tuple<String, Integer> tuple1 = ImmutableTuple.of("A", 1);
        Tuple<String, Integer> tuple2 = ImmutableTuple.of("A", 1);
        Tuple<String, Integer> tuple3 = ImmutableTuple.of("B", 2);

        assertEquals(tuple1, tuple2);
        assertNotEquals(tuple1, tuple3);

        assertEquals(tuple1.hashCode(), tuple2.hashCode());
        assertNotEquals(tuple1.hashCode(), tuple3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        Tuple<String, Integer> tuple = ImmutableTuple.of(null, null);

        assertNull(tuple.getFirst());
        assertNull(tuple.getSecond());
        assertTrue(tuple.isEmpty());
        assertTrue(tuple.contains(null));
    }

    @Test
    @DisplayName("Should produce correct string representation")
    void testToString() {
        Tuple<String, Integer> tuple = ImmutableTuple.of("Hello", 2024);
        assertEquals("(Hello, 2024)", tuple.toString());
    }

    @Test
    @DisplayName("Should swap elements correctly")
    void testSwap() {
        ImmutableTuple<String, Integer> original = ImmutableTuple.of("One", 2);
        ImmutableTuple<Integer, String> swapped = original.swap();

        assertEquals(2, swapped.getFirst());
        assertEquals("One", swapped.getSecond());
    }

    @Test
    @DisplayName("Should map elements correctly")
    void testMap() {
        ImmutableTuple<String, Integer> original = ImmutableTuple.of("5", 10);
        ImmutableTuple<Integer, String> mapped = original.map(Integer::parseInt, Object::toString);

        assertEquals(5, mapped.getFirst());
        assertEquals("10", mapped.getSecond());
    }

    @Test
    @DisplayName("Should map first element only")
    void testMapFirst() {
        ImmutableTuple<String, Integer> original = ImmutableTuple.of("Hello", 10);
        ImmutableTuple<Integer, Integer> mapped = original.mapFirst(String::length);

        assertEquals(5, mapped.getFirst());
        assertEquals(10, mapped.getSecond());
    }

    @Test
    @DisplayName("Should convert to a mutable tuple")
    void testToMutable() {
        ImmutableTuple<String, Integer> immutable = ImmutableTuple.of("A", 1);
        JXTuple<String, Integer> mutable = immutable.toMutable();

        assertEquals(immutable.getFirst(), mutable.getFirst());
        assertEquals(immutable.getSecond(), mutable.getSecond());
        assertInstanceOf(JXTuple.class, mutable);
    }
}