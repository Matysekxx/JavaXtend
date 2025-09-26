package org.javxtend.util;

import java.util.List;
import java.util.Objects;

/**
 * An immutable, general-purpose implementation of the {@link Tuple3} interface.
 * <p>
 * Once an instance of this class is created, its elements cannot be changed.
 * This makes it inherently thread-safe.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Create an immutable triple
 *     Tuple3&lt;String, Integer, Boolean&gt; record = ImmutableTuple3.of("data", 123, true);
 * </pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 */
public final class ImmutableTuple3<T1, T2, T3> implements Tuple3<T1, T2, T3> {

    private final T1 first;
    private final T2 second;
    private final T3 third;

    public ImmutableTuple3(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T1, T2, T3> ImmutableTuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new ImmutableTuple3<>(first, second, third);
    }

    @Override
    public T1 getFirst() {
        return first;
    }

    @Override
    public T2 getSecond() {
        return second;
    }

    @Override
    public T3 getThird() {
        return third;
    }

    public JXTuple3<T1, T2, T3> toMutable() {
        return new JXTuple3<>(first, second, third);
    }

    @Override
    public List<Object> toList() {
        return List.of(first, second, third);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableTuple3<?, ?, ?> that = (ImmutableTuple3<?, ?, ?>) o;
        return Objects.equals(first, that.first) &&
               Objects.equals(second, that.second) &&
               Objects.equals(third, that.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}