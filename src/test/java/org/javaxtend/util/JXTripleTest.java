package org.javaxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JXTripleTest {

    @Test
    @DisplayName("Should create a triple and get its elements")
    void testCreationAndGetters() {
        Triple<String, Integer, Boolean> triple = JXTriple.of("Test", 123, true);
        assertEquals("Test", triple.getFirst());
        assertEquals(123, triple.getSecond());
        assertTrue(triple.getThird());
    }

    @Test
    @DisplayName("Should modify elements using setters")
    void testSetters() {
        JXTriple<String, Integer, Boolean> triple = JXTriple.of("A", 1, false);
        triple.setFirst("B");
        triple.setSecond(2);
        triple.setThird(true);

        assertEquals("B", triple.getFirst());
        assertEquals(2, triple.getSecond());
        assertTrue(triple.getThird());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void testEqualsAndHashCode() {
        Triple<String, Integer, Boolean> triple1 = JXTriple.of("A", 1, true);
        Triple<String, Integer, Boolean> triple2 = JXTriple.of("A", 1, true);
        Triple<String, Integer, Boolean> triple3 = JXTriple.of("B", 2, false);

        assertEquals(triple1, triple2);
        assertNotEquals(triple1, triple3);

        assertEquals(triple1.hashCode(), triple2.hashCode());
        assertNotEquals(triple1.hashCode(), triple3.hashCode());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        Triple<String, Integer, Boolean> triple = JXTriple.of(null, null, null);

        assertNull(triple.getFirst());
        assertNull(triple.getSecond());
        assertNull(triple.getThird());
        assertTrue(triple.isEmpty());
    }

    @Test
    @DisplayName("Should produce correct string representation")
    void testToString() {
        Triple<String, Integer, Boolean> triple = JXTriple.of("Hello", 2024, false);
        assertEquals("(Hello, 2024, false)", triple.toString());
    }

    @Test
    @DisplayName("Should convert to a list")
    void testToList() {
        Triple<String, Integer, Boolean> triple = JXTriple.of("One", 2, true);
        List<Object> list = triple.toList();

        assertEquals(3, list.size());
        assertEquals("One", list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(true, list.get(2));
    }
}