package org.javaxtend.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Represents a range of integer values.
 * <p>
 * This class provides a convenient way to iterate over a sequence of integers,
 * with support for steps and reversed order. It implements {@link Iterable},
 * so it can be used directly in a for-each loop.
 * <p>
 * The range is inclusive of both the start and end values.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * // Simple iteration
 * for (int i : IntRange.of(1, 5)) { ... }
 *
 * // Stream processing
 * int sum = IntRange.of(1, 100).stream().filter(i -> i % 2 == 0).sum();
 *
 * // Iteration with a step
 * for (int i : IntRange.of(0, 10).step(2)) {
 *     // ... 0, 2, 4, 6, 8, 10
 * }
 *
 * // Reversed iteration
 * for (int i : IntRange.of(5, 1).reversed()) {
 *     // ... 5, 4, 3, 2, 1
 * }
 * }</pre></blockquote>
 */
public record IntRange(int start, int endInclusive, int step) implements Iterable<Integer> {

    public IntRange {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be zero.");
        }
    }

    /**
     * Creates a new range of integers from a starting value to an ending value (inclusive).
     * The default step is 1.
     *
     * @param start        The starting value of the range.
     * @param endInclusive The ending value of the range (inclusive).
     * @return A new {@code IntRange} instance.
     */
    public static IntRange of(int start, int endInclusive) {
        return new IntRange(start, endInclusive, 1);
    }

    /**
     * Returns a new range with the specified step.
     * The step must be positive. The direction of iteration is determined by
     * the start and end values.
     *
     * @param step The distance between consecutive values in the range. Must be positive.
     * @return A new {@code IntRange} with the specified step.
     * @throws IllegalArgumentException if the step is not positive.
     */
    public IntRange step(int step) {
        if (step <= 0) {
            throw new IllegalArgumentException("Step must be positive.");
        }
        return new IntRange(this.start, this.endInclusive, step);
    }

    /**
     * Returns a new range that iterates in the reverse order.
     *
     * @return A new, reversed {@code IntRange}.
     */
    public IntRange reversed() {
        if (this.step > 0) {
            return new IntRange(this.endInclusive, this.start, -this.step);
        } else {
            return new IntRange(this.endInclusive, this.start, this.step);
        }
    }

    /**
     * Checks if the range contains the given value.
     *
     * @param value The value to check.
     * @return {@code true} if the value is within the range and matches the step, {@code false} otherwise.
     */
    public boolean contains(int value) {
        if (step > 0) {
            return value >= start && value <= endInclusive && (value - start) % step == 0;
        } else {
            return value <= start && value >= endInclusive && (start - value) % -step == 0;
        }
    }

    /**
     * Checks if the range is empty. A range is empty if the end cannot be reached from the start.
     *
     * @return {@code true} if the range is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return (start > endInclusive && step > 0) || (start < endInclusive && step < 0);
    }

    /**
     * Returns a sequential {@link IntStream} for the elements in this range.
     *
     * @return a sequential {@code IntStream}.
     */
    public IntStream stream() {
        return StreamSupport.intStream(spliterator(), false);
    }

    /**
     * Returns a parallel {@link IntStream} for the elements in this range.
     *
     * @return a parallel {@code IntStream}.
     */
    public IntStream parallelStream() {
        return StreamSupport.intStream(spliterator(), true);
    }

    /**
     * Collects the elements of this range into a {@link List}.
     *
     * @return a new {@code List} containing all elements of the range.
     */
    public List<Integer> toList() {
        return stream().boxed().toList();
    }

    /**
     * Returns a sequential or parallel {@link IntStream} for the elements in this range.
     *
     * @param parallel if {@code true} then the returned stream is a parallel stream; otherwise, it is a sequential stream.
     * @return a sequential or parallel {@code IntStream}.
     */
    public IntStream stream(boolean parallel) {
        return StreamSupport.intStream(spliterator(), parallel);
    }

    private class IntRangeIterator implements Iterator<Integer> {
        private int current;

        IntRangeIterator() {
            this.current = start;
        }

        @Override
        public boolean hasNext() {
            if (isEmpty()) return false;
            if (step > 0) {
                return current <= endInclusive;
            } else {
                return current >= endInclusive;
            }
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the range.");
            }
            int value = current;
            current += step;
            return value;
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntRangeIterator();
    }

    @Override
    public Spliterator.OfInt spliterator() {
        return new IntRangeSpliterator(start, endInclusive, step);
    }

    private static class IntRangeSpliterator implements Spliterator.OfInt {
        private int current;
        private final int end;
        private final int step;

        IntRangeSpliterator(int start, int end, int step) {
            this.current = start;
            this.end = end;
            this.step = step;
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            Objects.requireNonNull(action);
            if ((step > 0 && current > end) || (step < 0 && current < end)) {
                return false;
            }
            action.accept(current);
            current += step;
            return true;
        }

        @Override
        public OfInt trySplit() {
            long remaining = estimateSize();
            if (remaining <= 1) {
                return null;
            }
            int midPoint = current + (int) (remaining / 2 / step) * step;
            IntRangeSpliterator newSpliterator = new IntRangeSpliterator(current, midPoint - step, step);
            this.current = midPoint;
            return newSpliterator;
        }

        @Override
        public long estimateSize() {
            if ((step > 0 && current > end) || (step < 0 && current < end)) return 0;
            return ((long) end - current) / step + 1;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT;
        }
    }
}