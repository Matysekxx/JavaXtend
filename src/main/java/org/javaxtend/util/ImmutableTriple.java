package org.javaxtend.util;

import java.util.List;
import java.util.function.Function;

/**
 * An immutable, general-purpose implementation of the {@link Triple} interface.
 * <p>
 * Once an instance of this class is created, its elements cannot be changed.
 * This makes it inherently thread-safe.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Create an immutable triple
 *     Triple&lt;String, Integer, Boolean&gt; record = ImmutableTriple.of("data", 123, true);
 * </pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 */
public record ImmutableTriple<T1, T2, T3>(T1 first, T2 second, T3 third) implements Triple<T1, T2, T3> {

    public static <T1, T2, T3> ImmutableTriple<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new ImmutableTriple<>(first, second, third);
    }

    /**
     * Applies mapping functions to all three elements of this triple, producing a new immutable triple.
     *
     * @param mapper1 a function to apply to the first element
     * @param mapper2 a function to apply to the second element
     * @param mapper3 a function to apply to the third element
     * @param <R1>    the type of the first element of the new triple
     * @param <R2>    the type of the second element of the new triple
     * @param <R3>    the type of the third element of the new triple
     * @return a new {@code ImmutableTriple} with the transformed elements
     */
    public <R1, R2, R3> ImmutableTriple<R1, R2, R3> map(
            Function<? super T1, ? extends R1> mapper1,
            Function<? super T2, ? extends R2> mapper2,
            Function<? super T3, ? extends R3> mapper3) {
        return new ImmutableTriple<>(mapper1.apply(first), mapper2.apply(second), mapper3.apply(third));
    }

    /**
     * Applies a mapping function to the first element, keeping the other elements unchanged.
     *
     * @param mapper a function to apply to the first element
     * @param <R>    the new type of the first element
     * @return a new {@code ImmutableTriple} with the transformed first element.
     */
    public <R> ImmutableTriple<R, T2, T3> mapFirst(Function<? super T1, ? extends R> mapper) {
        return new ImmutableTriple<>(mapper.apply(first), second, third);
    }

    /**
     * Applies a mapping function to the second element, keeping the other elements unchanged.
     *
     * @param mapper a function to apply to the second element
     * @param <R>    the new type of the second element
     * @return a new {@code ImmutableTriple} with the transformed second element.
     */
    public <R> ImmutableTriple<T1, R, T3> mapSecond(Function<? super T2, ? extends R> mapper) {
        return new ImmutableTriple<>(first, mapper.apply(second), third);
    }

    /**
     * Applies a mapping function to the third element, keeping the other elements unchanged.
     *
     * @param mapper a function to apply to the third element
     * @param <R>    the new type of the third element
     * @return a new {@code ImmutableTriple} with the transformed third element.
     */
    public <R> ImmutableTriple<T1, T2, R> mapThird(Function<? super T3, ? extends R> mapper) {
        return new ImmutableTriple<>(first, second, mapper.apply(third));
    }

    /**
     * Creates a new mutable {@link JXTriple} from the elements of this triple.
     *
     * @return a new mutable triple with the same elements.
     */
    public JXTriple<T1, T2, T3> toMutable() {
        return new JXTriple<>(first, second, third);
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

}