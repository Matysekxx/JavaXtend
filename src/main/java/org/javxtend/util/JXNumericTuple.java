package org.javxtend.util;

import java.util.Comparator;

/**
 * A specialized, mutable tuple where both elements are numbers.
 * It extends {@link JXTuple} and provides additional methods for common mathematical operations.
 *
 * @param <N> the type of the numbers in the tuple, which must extend {@link Number}
 */
public class JXNumericTuple<N extends Number> extends JXTuple<N, N> {

    /**
     * Constructs a new numeric tuple with the specified numbers.
     *
     * @param first the first number
     * @param second the second number
     */
    public JXNumericTuple(N first, N second) {
        super(first, second);
    }

    /**
     * Constructs a new numeric tuple by copying the elements from another numeric tuple.
     *
     * @param tuple the numeric tuple to copy elements from
     */
    public JXNumericTuple(JXNumericTuple<N> tuple) {
        super(tuple);
    }

    /**
     * Static factory method to create a new {@code JXNumericTuple} instance.
     *
     * @param first the first number
     * @param second the second number
     * @param <N> the type of the numbers
     * @return a new {@code JXNumericTuple} containing the provided numbers
     */
    public static <N extends Number> JXNumericTuple<N> of(N first, N second) {
        return new JXNumericTuple<>(first, second);
    }

    /**
     * Calculates the sum of the two numbers in the tuple.
     * The calculation is performed using their double-precision values.
     *
     * @return the sum as a {@code Double}, or {@code null} if either element is null.
     */
    public Double sum() {
        if (getFirst() == null || getSecond() == null) {
            return null;
        }
        return getFirst().doubleValue() + getSecond().doubleValue();
    }

    /**
     * Calculates the product of the two numbers in the tuple.
     * The calculation is performed using their double-precision values.
     *
     * @return the product as a {@code Double}, or {@code null} if either element is null.
     */
    public Double product() {
        if (getFirst() == null || getSecond() == null) {
            return null;
        }
        return getFirst().doubleValue() * getSecond().doubleValue();
    }

    /**
     * Calculates the average of the two numbers in the tuple.
     *
     * @return the average as a {@code Double}, or {@code null} if the sum cannot be calculated.
     */
    public Double average() {
        Double sum = sum();
        return sum != null ? sum / 2.0 : null;
    }

    /**
     * Returns the smaller of the two numbers in the tuple.
     * If one element is null, the other is returned.
     *
     * @return the minimum value, or {@code null} if both are null.
     */
    public N min() {
        if (getFirst() == null) return getSecond();
        if (getSecond() == null) return getFirst();
        return getFirst().doubleValue() <= getSecond().doubleValue() ? getFirst() : getSecond();
    }

    /**
     * Returns the larger of the two numbers in the tuple.
     * If one element is null, the other is returned.
     *
     * @return the maximum value, or {@code null} if both are null.
     */
    public N max() {
        if (getFirst() == null) return getSecond();
        if (getSecond() == null) return getFirst();
        return getFirst().doubleValue() >= getSecond().doubleValue() ? getFirst() : getSecond();
    }
}
