package org.javaxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntervalTest {

    @Test
    @DisplayName("of() should create an interval with correct bounds")
    void of_createsInterval() {
        Interval<Integer> interval = Interval.of(10, 20);
        assertEquals(10, interval.getStart());
        assertEquals(20, interval.getEnd());
    }

    @Test
    @DisplayName("of() should throw exception if start is after end")
    void of_throwsExceptionForInvalidBounds() {
        assertThrows(IllegalArgumentException.class, () -> Interval.of(20, 10));
    }

    @Test
    @DisplayName("contains() should return true for values within the interval")
    void contains_returnsTrueForValueInside() {
        Interval<Integer> interval = Interval.of(10, 20);
        assertTrue(interval.contains(15));
        assertTrue(interval.contains(10), "Should be inclusive of start");
        assertTrue(interval.contains(20), "Should be inclusive of end");
    }

    @Test
    @DisplayName("contains() should return false for values outside the interval")
    void contains_returnsFalseForValueOutside() {
        Interval<Integer> interval = Interval.of(10, 20);
        assertFalse(interval.contains(5));
        assertFalse(interval.contains(25));
    }

    @Test
    @DisplayName("overlaps() should correctly identify overlapping intervals")
    void overlaps_identifiesOverlaps() {
        Interval<Integer> main = Interval.of(10, 20);

        assertTrue(main.overlaps(Interval.of(15, 25)));
        assertTrue(main.overlaps(Interval.of(5, 15)));

        assertTrue(main.overlaps(Interval.of(12, 18)));
        assertTrue(Interval.of(12, 18).overlaps(main));

        assertTrue(main.overlaps(Interval.of(20, 30)));
        assertTrue(main.overlaps(Interval.of(5, 10)));
    }

    @Test
    @DisplayName("overlaps() should return false for non-overlapping intervals")
    void overlaps_returnsFalseForNoOverlap() {
        Interval<Integer> main = Interval.of(10, 20);
        assertFalse(main.overlaps(Interval.of(21, 30)));
        assertFalse(main.overlaps(Interval.of(0, 9)));
    }
}