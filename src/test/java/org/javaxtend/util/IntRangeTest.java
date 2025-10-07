package org.javaxtend.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntRangeTest {

    private List<Integer> toList(IntRange range) {
        List<Integer> list = new ArrayList<>();
        for (int i : range) {
            list.add(i);
        }
        return list;
    }

    @Test
    void of_iteratesCorrectly() {
        IntRange range = IntRange.of(1, 5);
        assertEquals(List.of(1, 2, 3, 4, 5), toList(range));
    }

    @Test
    void of_singleElementRange() {
        IntRange range = IntRange.of(3, 3);
        assertEquals(List.of(3), toList(range));
    }

    @Test
    void step_iteratesWithStep() {
        IntRange range = IntRange.of(0, 10).step(2);
        assertEquals(List.of(0, 2, 4, 6, 8, 10), toList(range));
    }

    @Test
    void step_handlesUnevenEnd() {
        IntRange range = IntRange.of(1, 8).step(3);
        assertEquals(List.of(1, 4, 7), toList(range));
    }

    @Test
    void reversed_iteratesBackwards() {
        IntRange range = IntRange.of(1, 5).reversed();
        assertEquals(List.of(5, 4, 3, 2, 1), toList(range));
    }

    @Test
    void reversed_withStep_iteratesBackwardsWithStep() {
        IntRange range = IntRange.of(0, 10).step(3).reversed();
        assertEquals(List.of(10, 7, 4, 1), toList(range));
    }

    @Test
    void contains_returnsTrueForValueInRange() {
        IntRange range = IntRange.of(0, 10).step(2);
        assertTrue(range.contains(4));
        assertTrue(range.contains(0));
        assertTrue(range.contains(10));
    }

    @Test
    void contains_returnsFalseForValueNotInRange() {
        IntRange range = IntRange.of(0, 10).step(2);
        assertFalse(range.contains(5));
        assertFalse(range.contains(11));
        assertFalse(range.contains(-1));
    }

    @Test
    void isEmpty_returnsTrueForInvalidRange() {
        IntRange range = IntRange.of(10, 1);
        assertTrue(range.isEmpty());
        assertEquals(0, toList(range).size());
    }

    @Test
    void isEmpty_returnsFalseForValidRange() {
        assertFalse(IntRange.of(1, 5).isEmpty());
        assertFalse(IntRange.of(1, 5).reversed().isEmpty());
    }

    @Test
    void step_throwsExceptionForNonPositiveStep() {
        assertThrows(IllegalArgumentException.class, () -> IntRange.of(1, 5).step(0));
        assertThrows(IllegalArgumentException.class, () -> IntRange.of(1, 5).step(-1));
    }
}