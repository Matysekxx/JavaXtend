package org.javaxtend.data;

import org.javaxtend.functional.Maybe;
import org.javaxtend.util.IntRange;
import org.javaxtend.validation.Guard;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * A one-dimensional labeled array capable of holding any data type, inspired by pandas.Series.
 * <p>
 * A Series is immutable and consists of three main components:
 * <ul>
 *     <li><b>values:</b> An immutable list of values.</li>
 *     <li><b>index:</b> An immutable list of labels for the data.</li>
 *     <li><b>name:</b> An optional name for the Series.</li>
 * </ul>
 *
 * @param <T> The type of data held in the series.
 */
public class Series<T> {
    private final List<T> values;
    private final List<?> index;
    private final String name;
    private final Map<Object, Integer> indexMap;

    public Series(List<T> values, List<?> index, String name) {
        Guard.against().isNull(values, "Values cannot be null");
        Guard.against().isNull(index, "Index cannot be null");

        if (values.size() != index.size()) {
            throw new IllegalArgumentException("Values and index must have the same length.");
        }

        this.values = List.copyOf(values);
        this.index = List.copyOf(index);
        this.name = name;
        this.indexMap = buildIndexMap();
    }

    public Series(List<T> values) {
        this(values, IntRange.of(0, values.size() - 1).toList(), null);
    }

    public Series(T[] values, Object[] index) {
        this(List.of(values), List.of(index), null);
    }



    private Map<Object, Integer> buildIndexMap() {
        final Map<Object, Integer> map = new HashMap<>(index.size());
        for (int i = 0; i < index.size(); i++) {
            map.put(index.get(i), i);
        }
        return Map.copyOf(map);
    }

    public List<T> values() {
        return values;
    }

    public List<?> index() {
        return index;
    }

    public T get(Object key) {
        final Integer position = indexMap.get(key);
        if (position == null) {
            throw new KeyException("Key not found in index: " + key);
        }
        return values.get(position);
    }

    /**
     * Returns the number of elements in the series.
     * @return the number of elements.
     */
    public int size() {
        return values.size();
    }

    /**
     * Applies a function to each value in the Series and returns a new Series with the transformed values.
     * The index is preserved.
     *
     * @param mapper The function to apply to each value.
     * @param <R> The type of the values in the new Series.
     * @return A new Series with the transformed values.
     */
    public <R> Series<R> map(Function<? super T, ? extends R> mapper) {
        Guard.against().isNull(mapper, "mapper");
        List<R> newValues = this.values.stream().map(mapper).collect(Collectors.toList());
        return new Series<>(newValues, this.index, this.name);
    }

    /**
     * Filters the Series, returning a new Series containing only the elements that match the given predicate.
     *
     * @param predicate The predicate to apply to each value.
     * @return A new, filtered Series.
     */
    public Series<T> filter(Predicate<? super T> predicate) {
        Guard.against().isNull(predicate, "predicate");
        List<T> newValues = new ArrayList<>();
        List<Object> newIndex = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            T value = values.get(i);
            if (predicate.test(value)) {
                newValues.add(value);
                newIndex.add(this.index.get(i));
            }
        }
        return new Series<>(newValues, newIndex, this.name);
    }

    /**
     * Calculates the sum of the values in the series.
     * This method is only applicable to series of {@link Number} type.
     *
     * @return The sum of the values as a double.
     * @throws UnsupportedOperationException if the series does not contain numbers.
     */
    public double sum() {
        return getNumericStream().sum();
    }

    /**
     * Calculates the average (mean) of the values in the series.
     * This method is only applicable to series of {@link Number} type.
     *
     * @return The average of the values as a double. Returns NaN if the series is empty.
     * @throws UnsupportedOperationException if the series does not contain numbers.
     */
    public double mean() {
        return getNumericStream().average().orElse(Double.NaN);
    }

    /**
     * Finds the minimum value in the series.
     * This method is only applicable to series of {@link Comparable} types.
     *
     * @return A {@code Maybe} containing the minimum value, or {@code Maybe.nothing()} if the series is empty.
     * @throws ClassCastException if the elements are not comparable.
     */
    public Maybe<T> min() {
        if (values.isEmpty() || !(values.getFirst() instanceof Comparable<?>)) {
            return Maybe.nothing();
        }

        var minVal = values.getFirst();
        for (int i = 1; i < values.size(); i++) {
            var current = values.get(i);
            if (((Comparable<T>) current).compareTo(minVal) < 0) {
                minVal = current;
            }
        }
        return Maybe.just(minVal);
    }

    /**
     * Finds the maximum value in the series.
     * This method is only applicable to series of {@link Comparable} types.
     *
     * @return A {@code Maybe} containing the maximum value, or {@code Maybe.nothing()} if the series is empty.
     * @throws ClassCastException if the elements are not comparable.
     */
    public Maybe<T> max() {
        if (values.isEmpty() || !(values.getFirst() instanceof Comparable<?>)) {
            return Maybe.nothing();
        }

        var maxVal = values.getFirst();
        for (int i = 1; i < values.size(); i++) {
            var current = values.get(i);
            if (((Comparable<T>) current).compareTo(maxVal) > 0) {
                maxVal = current;
            }
        }
        return Maybe.just(maxVal);
    }

    /**
     * Generates descriptive statistics for a numeric series.
     *
     * @return A new {@code Series} containing statistics like count, mean, min, and max.
     * @throws UnsupportedOperationException if the series does not contain numbers.
     */
    public Series<Double> describe() {
        final DoubleSummaryStatistics stats = getNumericStream().summaryStatistics();
        final var newIndex = List.of("count", "mean", "std", "min", "25%", "50%", "75%", "max");
        final var newValues = List.of(
                (double) stats.getCount(),
                stats.getAverage(),
                Double.NaN,
                stats.getMin(),
                Double.NaN,
                Double.NaN,
                Double.NaN,
                stats.getMax()
        );
        return new Series<>(newValues, newIndex, "describe");
    }

    private DoubleStream getNumericStream() {
        if (values.isEmpty() || !(values.getFirst() instanceof Number)) {
            throw new UnsupportedOperationException("This operation is only supported for series of numbers.");
        }
        return this.values.stream().mapToDouble(v -> ((Number) v).doubleValue());
    }

    @Override
    public String toString() {
        final var sb = new StringBuilder();
        if (name != null && !name.isBlank()) {
            sb.append("Name: ").append(name).append("\n");
        }
        for (int i = 0; i < index.size(); i++) {
            sb.append(index.get(i)).append("\t").append(values.get(i)).append("\n");
        }
        return sb.toString();
    }
}
