package org.javxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JXTupleTest {

    @Test
    @DisplayName("Should create a tuple and get its elements")
    void testCreationAndGetters() {
        Tuple<String, Integer> tuple = JXTuple.of("Test", 123);
        assertEquals("Test", tuple.getFirst());
        assertEquals(123, tuple.getSecond());
    }

    @Test
    @DisplayName("Should modify elements using setters")
    void testSetters() {
        JXTuple<String, Integer> tuple = JXTuple.of("Initial", 1);
        tuple.setFirst("Modified");
        tuple.setSecond(2);

        assertEquals("Modified", tuple.getFirst());
        assertEquals(2, tuple.getSecond());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        Tuple<String, Integer> tuple1 = JXTuple.of("A", 1);
        Tuple<String, Integer> tuple2 = JXTuple.of("A", 1);
        Tuple<String, Integer> tuple3 = JXTuple.of("B", 2);

        assertEquals(tuple1, tuple2);
        assertNotEquals(tuple1, tuple3);

        assertEquals(tuple1.hashCode(), tuple2.hashCode());
        assertNotEquals(tuple1.hashCode(), tuple3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        Tuple<String, Integer> tuple = JXTuple.of(null, null);

        assertNull(tuple.getFirst());
        assertNull(tuple.getSecond());
        assertTrue(tuple.isEmpty());
        assertTrue(tuple.contains(null));
    }

    @Test
    @DisplayName("Should produce correct string representation")
    void testToString() {
        Tuple<String, Integer> tuple = JXTuple.of("Hello", 2024);
        assertEquals("(Hello, 2024)", tuple.toString());
    }

    @Test
    @DisplayName("Should swap elements correctly")
    void testSwap() {
        JXTuple<String, Integer> original = JXTuple.of("One", 2);
        JXTuple<Integer, String> swapped = original.swap();

        assertEquals(2, swapped.getFirst());
        assertEquals("One", swapped.getSecond());
    }

    @Test
    @DisplayName("Should map elements correctly")
    void testMap() {
        JXTuple<String, Integer> original = JXTuple.of("5", 10);
        JXTuple<Integer, String> mapped = original.map(Integer::parseInt, Object::toString);

        assertEquals(5, mapped.getFirst());
        assertEquals("10", mapped.getSecond());
    }

    @Test
    @DisplayName("Should map second element only")
    void testMapSecond() {
        JXTuple<String, Integer> original = JXTuple.of("Hello", 10);
        JXTuple<String, String> mapped = original.mapSecond(val -> "Value: " + val);

        assertEquals("Hello", mapped.getFirst());
        assertEquals("Value: 10", mapped.getSecond());
    }

    @Test
    @DisplayName("Should convert to an immutable tuple")
    void testToImmutable() {
        Tuple<String, Integer> mutable = JXTuple.of("A", 1);
        ImmutableTuple<String, Integer> immutable = ((JXTuple<String, Integer>) mutable).toImmutable();

        assertEquals(mutable.getFirst(), immutable.getFirst());
        assertEquals(mutable.getSecond(), immutable.getSecond());
        assertInstanceOf(ImmutableTuple.class, immutable);
    }
}