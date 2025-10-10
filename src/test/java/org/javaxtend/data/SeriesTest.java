package org.javaxtend.data;

import org.javaxtend.functional.Maybe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeriesTest {

    @Test
    @DisplayName("Constructor should create a Series with matching lengths")
    void constructor_withMatchingLengths_createsSeries() {
        var values = List.of("a", "b");
        var index = List.of(1, 2);
        var series = new Series<>(values, index, "test");

        assertEquals(2, series.size());
        assertEquals(values, series.values());
        assertEquals(index, series.index());
    }

    @Test
    @DisplayName("Constructor should throw exception for mismatched lengths")
    void constructor_withMismatchedLengths_throwsException() {
        var values = List.of("a", "b");
        var index = List.of(1);
        assertThrows(IllegalArgumentException.class, () -> new Series<>(values, index, "test"));
    }

    @Test
    @DisplayName("Constructor should create a default integer index")
    void constructor_withDefaultIndex_createsSeries() {
        var values = List.of("a", "b", "c");
        var series = new Series<>(values);

        assertEquals(3, series.size());
        assertEquals(List.of(0, 1, 2), series.index());
    }

    @Test
    @DisplayName("Series should be immutable")
    void seriesIsImmutable() {
        var originalValues = new ArrayList<>(List.of("a", "b"));
        var originalIndex = new ArrayList<>(List.of(1, 2));
        var series = new Series<>(originalValues, originalIndex, "test");

        originalValues.add("c");
        originalIndex.add(3);

        assertEquals(2, series.size(), "Series size should not change");
        assertNotEquals(originalValues, series.values(), "Series values should be an immutable copy");
    }

    @Test
    @DisplayName("get() should return value for a valid key")
    void get_withValidKey_returnsValue() {
        var values = List.of(10, 20);
        var index = List.of("x", "y");
        var series = new Series<>(values, index, "test");

        assertEquals(20, series.get("y"));
    }

    @Test
    @DisplayName("get() should throw KeyException for an invalid key")
    void get_withInvalidKey_throwsException() {
        var series = new Series<>(List.of(10), List.of("x"), "test");
        assertThrows(KeyException.class, () -> series.get("z"));
    }

    @Test
    @DisplayName("map() should transform values and preserve index")
    void map_transformsValues() {
        var series = new Series<>(List.of("a", "bb", "ccc"), List.of(1, 2, 3), "lengths");
        Series<Integer> mapped = series.map(String::length);

        assertEquals(List.of(1, 2, 3), mapped.values());
        assertEquals(series.index(), mapped.index());
    }

    @Test
    @DisplayName("filter() should return a new Series with matching elements")
    void filter_returnsMatchingElements() {
        var series = new Series<>(List.of(10, 20, 30, 40), List.of("a", "b", "c", "d"), "numbers");
        Series<Integer> filtered = series.filter(v -> v > 25);

        assertEquals(2, filtered.size());
        assertEquals(List.of(30, 40), filtered.values());
        assertEquals(List.of("c", "d"), filtered.index());
    }

    @Test
    @DisplayName("sum() and mean() should work for numeric Series")
    void sumAndMean_forNumericSeries() {
        var series = new Series<>(List.of(10.0, 20.0, 30.0));
        assertEquals(60.0, series.sum());
        assertEquals(20.0, series.mean());
    }

    @Test
    @DisplayName("sum() should throw exception for non-numeric Series")
    void sum_forNonNumericSeries_throwsException() {
        var series = new Series<>(List.of("a", "b"));
        assertThrows(UnsupportedOperationException.class, series::sum);
    }

    @Test
    @DisplayName("min() and max() should find correct values for Comparable Series")
    void minAndMax_forComparableSeries() {
        var series = new Series<>(List.of(30, 10, 20));
        assertEquals(10, series.min().unwrap());
        assertEquals(30, series.max().unwrap());
    }

    @Test
    @DisplayName("min() and max() should return Nothing for empty Series")
    void minAndMax_forEmptySeries_returnNothing() {
        var emptySeries = new Series<>(List.<Integer>of());
        assertTrue(emptySeries.min().isNothing());
        assertTrue(emptySeries.max().isNothing());
    }

    private record NonComparable(int value) {}

    @Test
    @DisplayName("min() and max() should return Nothing for non-comparable Series")
    void minAndMax_forNonComparableSeries_returnNothing() {
        var series = new Series<>(List.of(new NonComparable(1), new NonComparable(2)));
        assertTrue(series.min().isNothing());
        assertTrue(series.max().isNothing());
    }

    @Test
    @DisplayName("describe() should return correct statistics for numeric Series")
    void describe_returnsCorrectStats() {
        var series = new Series<>(List.of(10.0, 20.0, 60.0, 10.0));
        Series<Double> stats = series.describe();

        assertEquals(4.0, stats.get("count"));
        assertEquals(25.0, stats.get("mean"));
        assertEquals(10.0, stats.get("min"));
        assertEquals(60.0, stats.get("max"));
    }

    @Test
    @DisplayName("toString() should format correctly with a name")
    void toString_withName() {
        var series = new Series<>(List.of("x"), List.of(1), "MySeries");
        String expected = """
                Name: MySeries
                1\tx
                """;
        assertEquals(expected, series.toString());
    }

    @Test
    @DisplayName("toString() should format correctly without a name")
    void toString_withoutName() {
        var series = new Series<>(List.of("x"), List.of(1), null);
        String expected = """
                1\tx
                """;
        assertEquals(expected, series.toString());
    }
}