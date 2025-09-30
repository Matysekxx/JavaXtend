package org.javaxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableTripleTest {

    @Test
    @DisplayName("Should create an immutable triple and get its elements")
    void testCreationAndGetters() {
        Triple<String, Integer, Boolean> triple = ImmutableTriple.of("Test", 123, true);
        assertEquals("Test", triple.getFirst());
        assertEquals(123, triple.getSecond());
        assertTrue(triple.getThird());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        Triple<String, Integer, Boolean> triple1 = ImmutableTriple.of("A", 1, true);
        Triple<String, Integer, Boolean> triple2 = ImmutableTriple.of("A", 1, true);
        Triple<String, Integer, Boolean> triple3 = ImmutableTriple.of("B", 2, false);

        assertEquals(triple1, triple2);
        assertNotEquals(triple1, triple3);

        assertEquals(triple1.hashCode(), triple2.hashCode());
        assertNotEquals(triple1.hashCode(), triple3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        Triple<String, Integer, Boolean> triple = ImmutableTriple.of(null, null, null);

        assertNull(triple.getFirst());
        assertNull(triple.getSecond());
        assertNull(triple.getThird());
        assertTrue(triple.isEmpty());
    }

    @Test
    @DisplayName("Should produce correct string representation")
    void testToString() {
        Triple<String, Integer, Boolean> triple = ImmutableTriple.of("Hello", 2024, false);
        assertEquals("(Hello, 2024, false)", triple.toString());
    }

    @Test
    @DisplayName("Should convert to a list")
    void testToList() {
        Triple<String, Integer, Boolean> triple = ImmutableTriple.of("One", 2, true);
        List<Object> list = triple.toList();

        assertEquals(3, list.size());
        assertEquals("One", list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(true, list.get(2));
    }

    @Test
    @DisplayName("Should convert to a mutable tuple")
    void testToMutable() {
        ImmutableTriple<String, Integer, Boolean> immutable = ImmutableTriple.of("A", 1, true);
        JXTriple<String, Integer, Boolean> mutable = immutable.toMutable();

        assertEquals(immutable.getFirst(), mutable.getFirst());
        assertEquals(immutable.getSecond(), mutable.getSecond());
        assertEquals(immutable.getThird(), mutable.getThird());
        assertInstanceOf(JXTriple.class, mutable);
    }
}