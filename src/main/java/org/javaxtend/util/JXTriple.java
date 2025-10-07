package org.javaxtend.util;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A mutable, general-purpose implementation of the {@link Triple} interface.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * JXTriple<Integer, Integer, Integer> point = JXTriple.of(10, 20, 30);
 * point.setThird(35);
 * }</pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 */
public final class JXTriple<T1, T2, T3> implements Triple<T1, T2, T3>, Serializable {

    private T1 first;
    private T2 second;
    private T3 third;

    public JXTriple(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T1, T2, T3> JXTriple<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new JXTriple<>(first, second, third);
    }

    @Override
    public T1 first() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    @Override
    public T2 second() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public T3 third() {
        return third;
    }

    public void setThird(T3 third) {
        this.third = third;
    }

    /**
     * Applies mapping functions to all three elements of this triple, producing a new mutable triple.
     */
    public <R1, R2, R3> JXTriple<R1, R2, R3> map(
            Function<? super T1, ? extends R1> mapper1,
            Function<? super T2, ? extends R2> mapper2,
            Function<? super T3, ? extends R3> mapper3) {
        return new JXTriple<>(mapper1.apply(first), mapper2.apply(second), mapper3.apply(third));
    }

    /**
     * Applies a mapping function to the first element, keeping the other elements unchanged.
     */
    public <R> JXTriple<R, T2, T3> mapFirst(Function<? super T1, ? extends R> mapper) {
        return new JXTriple<>(mapper.apply(first), second, third);
    }

    /**
     * Applies a mapping function to the second element, keeping the other elements unchanged.
     */
    public <R> JXTriple<T1, R, T3> mapSecond(Function<? super T2, ? extends R> mapper) {
        return new JXTriple<>(first, mapper.apply(second), third);
    }

    /**
     * Applies a mapping function to the third element, keeping the other elements unchanged.
     */
    public <R> JXTriple<T1, T2, R> mapThird(Function<? super T3, ? extends R> mapper) {
        return new JXTriple<>(first, second, mapper.apply(third));
    }

    /**
     * Creates a new immutable {@link ImmutableTriple} from the elements of this triple.
     * @return a new immutable triple with the same elements.
     */
    public ImmutableTriple<T1, T2, T3> toImmutable() {
        return new ImmutableTriple<>(first, second, third);
    }

    @Override
    public List<Object> toList() {
        return List.of(first, second, third);
    }

    @Override
    public Object[] toArray() {
        return new Object[]{first, second, third};
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple<?, ?, ?> that)) return false;
        return Objects.equals(first, that.first()) &&
               Objects.equals(second, that.second()) &&
               Objects.equals(third, that.third());
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}