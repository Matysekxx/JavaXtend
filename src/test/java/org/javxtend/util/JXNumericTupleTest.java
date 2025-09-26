package org.javxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JXNumericTupleTest {

    @Test
    @DisplayName("Should create a numeric tuple and allow modification")
    void testCreationAndSetters() {
        JXNumericTuple<Integer> tuple = JXNumericTuple.of(10, 20);
        assertEquals(10, tuple.getFirst());
        assertEquals(20, tuple.getSecond());

        tuple.setFirst(100);
        assertEquals(100, tuple.getFirst());
    }

    @Test
    @DisplayName("Should calculate the sum correctly")
    void testSum() {
        JXNumericTuple<Integer> intTuple = JXNumericTuple.of(10, 20);
        assertEquals(30.0, intTuple.sum());

        JXNumericTuple<Double> doubleTuple = JXNumericTuple.of(10.5, 20.5);
        assertEquals(31.0, doubleTuple.sum());

        JXNumericTuple<Number> mixedTuple = JXNumericTuple.of(5, 15.5);
        assertEquals(20.5, mixedTuple.sum());
    }

    @Test
    @DisplayName("Sum should return null if an element is null")
    void testSumWithNull() {
        JXNumericTuple<Integer> tuple = JXNumericTuple.of(10, null);
        assertNull(tuple.sum());
    }

    @Test
    @DisplayName("Should calculate the product correctly")
    void testProduct() {
        JXNumericTuple<Integer> intTuple = JXNumericTuple.of(5, 4);
        assertEquals(20.0, intTuple.product());

        JXNumericTuple<Double> doubleTuple = JXNumericTuple.of(2.5, 4.0);
        assertEquals(10.0, doubleTuple.product());
    }

    @Test
    @DisplayName("Should calculate the average correctly")
    void testAverage() {
        JXNumericTuple<Integer> intTuple = JXNumericTuple.of(10, 20);
        assertEquals(15.0, intTuple.average());

        JXNumericTuple<Double> doubleTuple = JXNumericTuple.of(10.0, 11.0);
        assertEquals(10.5, doubleTuple.average());
    }

    @Test
    @DisplayName("Should find the minimum value")
    void testMin() {
        JXNumericTuple<Integer> intTuple = JXNumericTuple.of(100, 10);
        assertEquals(10, intTuple.min());

        JXNumericTuple<Double> doubleTuple = JXNumericTuple.of(99.9, 99.8);
        assertEquals(99.8, doubleTuple.min());
    }

    @Test
    @DisplayName("Min should handle null values")
    void testMinWithNull() {
        JXNumericTuple<Integer> tuple1 = JXNumericTuple.of(null, 10);
        assertEquals(10, tuple1.min());

        JXNumericTuple<Integer> tuple2 = JXNumericTuple.of(5, null);
        assertEquals(5, tuple2.min());

        JXNumericTuple<Integer> tuple3 = JXNumericTuple.of(null, null);
        assertNull(tuple3.min());
    }

    @Test
    @DisplayName("Should find the maximum value")
    void testMax() {
        JXNumericTuple<Integer> intTuple = JXNumericTuple.of(100, 10);
        assertEquals(100, intTuple.max());

        JXNumericTuple<Double> doubleTuple = JXNumericTuple.of(99.9, 100.1);
        assertEquals(100.1, doubleTuple.max());
    }
}