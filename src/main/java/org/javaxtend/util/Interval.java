package org.javaxtend.util;

import org.javaxtend.validation.Guard;

import java.util.Objects;

/**
 * Represents a generic, immutable, inclusive interval [start, end].
 * <p>
 * This class works with any {@link Comparable} type and provides
 * methods to check for containment, overlaps, and basic equality.
 *
 * <h2>Example of Usage:</h2>
 * <pre>{@code
 * Interval<Integer> range = Interval.of(10, 20);
 * boolean contains15 = range.contains(15); // true
 *
 * Interval<LocalTime> meeting = Interval.of(
 *     LocalTime.of(9, 0),
 *     LocalTime.of(10, 0)
 * );
 * boolean overlaps = meeting.overlaps(
 *     Interval.of(LocalTime.of(9, 30), LocalTime.of(10, 30))
 * ); // true
 * }</pre>
 *
 * @param <T> The type of the interval's bounds, must implement Comparable.
 */
public final class Interval<T extends Comparable<T>> {

    private final T start;
    private final T end;

    private Interval(T start, T end) {
        Guard.against().isNull(start, "start");
        Guard.against().isNull(end, "end");
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start of interval cannot be after end.");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Factory method to create a new interval [start, end].
     *
     * @param start The start of the interval (inclusive).
     * @param end The end of the interval (inclusive).
     * @param <T> The type of the interval's bounds.
     * @return A new Interval instance.
     */
    public static <T extends Comparable<T>> Interval<T> of(T start, T end) {
        return new Interval<>(start, end);
    }

    /**
     * Returns the start of the interval.
     *
     * @return The inclusive start value.
     */
    public T getStart() {
        return start;
    }

    /**
     * Returns the end of the interval.
     *
     * @return The inclusive end value.
     */
    public T getEnd() {
        return end;
    }

    /**
     * Checks if the given value is contained within this interval (inclusive).
     *
     * @param value The value to check.
     * @return {@code true} if the value is within the interval, {@code false} otherwise.
     */
    public boolean contains(T value) {
        Guard.against().isNull(value, "value");
        return start.compareTo(value) <= 0 && end.compareTo(value) >= 0;
    }

    /**
     * Checks if this interval overlaps with another interval.
     * <p>
     * Two intervals [a, b] and [c, d] overlap if they have at least one point in common:
     * <pre>
     * overlap condition: a <= d AND c <= b
     * </pre>
     *
     * @param other The other interval to check against.
     * @return {@code true} if the intervals overlap, {@code false} otherwise.
     */
    public boolean overlaps(Interval<T> other) {
        Guard.against().isNull(other, "other interval");
        return this.start.compareTo(other.end) <= 0 && other.start.compareTo(this.end) <= 0;
    }

    /**
     * Checks if this interval completely encloses another interval.
     * <p>
     * An interval [a, b] encloses [c, d] if a <= c AND b >= d.
     *
     * @param other The other interval to check against.
     * @return {@code true} if this interval encloses the other, {@code false} otherwise.
     */
    public boolean encloses(Interval<T> other) {
        Guard.against().isNull(other, "other interval");
        return this.start.compareTo(other.start) <= 0 && this.end.compareTo(other.end) >= 0;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interval<?> interval)) return false;
        return start.equals(interval.start) && end.equals(interval.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
