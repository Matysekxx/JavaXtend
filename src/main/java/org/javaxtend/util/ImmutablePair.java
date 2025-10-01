package org.javaxtend.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * An immutable, general-purpose implementation of the {@link Pair} interface.
 * <p>
 * Once an instance of this class is created, its elements cannot be changed.
 * This makes it inherently thread-safe and reliable for use as keys in maps
 * or elements in sets.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Create an immutable pair
 *     Pair&lt;String, Integer&gt; config = ImmutablePair.of("timeout", 5000);
 * </pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 */
public final class ImmutablePair<T1, T2> implements Pair<T1, T2> {

    private final T1 first;
    private final T2 second;

    /**
     * Constructs a new immutable tuple with the specified elements.
     *
     * @param first  the first element
     * @param second the second element
     */
    public ImmutablePair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Static factory method to create a new {@code ImmutablePair} instance.
     *
     * @param first  the first element
     * @param second the second element
     * @param <T1>   the type of the first element
     * @param <T2>   the type of the second element
     * @return a new {@code ImmutablePair} containing the provided elements
     */
    public static <T1, T2> ImmutablePair<T1, T2> of(T1 first, T2 second) {
        return new ImmutablePair<>(first, second);
    }

    @Override
    public T1 getFirst() {
        return first;
    }

    @Override
    public T2 getSecond() {
        return second;
    }

    /**
     * Swaps the elements of the tuple and returns a new immutable tuple.
     *
     * @return a new {@code ImmutablePair} with the first and second elements swapped.
     */
    public ImmutablePair<T2, T1> swap() {
        return new ImmutablePair<>(second, first);
    }

    /**
     * Applies mapping functions to both elements of this tuple, producing a new immutable tuple.
     *
     * @return a new {@code ImmutablePair} with the transformed elements.
     */
    public <R1, R2> ImmutablePair<R1, R2> map(Function<? super T1, ? extends R1> mapper1, Function<? super T2, ? extends R2> mapper2) {
        return new ImmutablePair<>(mapper1.apply(first), mapper2.apply(second));
    }

    /**
     * Applies a mapping function to the first element, keeping the second element unchanged.
     *
     * @param mapper a function to apply to the first element
     * @param <R> the new type of the first element
     * @return a new {@code ImmutablePair} with the transformed first element.
     */
    public <R> ImmutablePair<R, T2> mapFirst(Function<? super T1, ? extends R> mapper) {
        return new ImmutablePair<>(mapper.apply(first), second);
    }

    /**
     * Applies a mapping function to the second element, keeping the first element unchanged.
     *
     * @param mapper a function to apply to the second element
     * @param <R> the new type of the second element
     * @return a new {@code ImmutablePair} with the transformed second element.
     */
    public <R> ImmutablePair<T1, R> mapSecond(Function<? super T2, ? extends R> mapper) {
        return new ImmutablePair<>(first, mapper.apply(second));
    }

    /**
     * Creates a new mutable {@link JXPair} from the elements of this tuple.
     *
     * @return a new mutable tuple with the same elements.
     */
    public JXPair<T1, T2> toMutable() {
        return new JXPair<>(first, second);
    }

    @Override
    public Object[] toArray() {
        return new Object[]{first, second};
    }

    @Override
    public List<Object> toList() {
        return List.of(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }
}