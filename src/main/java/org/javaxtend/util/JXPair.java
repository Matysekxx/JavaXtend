package org.javaxtend.util;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A mutable, general-purpose implementation of the {@link Pair} interface.
 * <p>
 * This class is not thread-safe. It is designed for performance-critical scenarios
 * where object allocation needs to be minimized. For a thread-safe, immutable
 * alternative, see {@link ImmutablePair}.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * JXPair<String, Integer> userProfile = JXPair.of("John Doe", 30);
 * userProfile.setSecond(31); // Update the age
 * }</pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 */
public final class JXPair<T1, T2> implements Pair<T1, T2>, Serializable {

    private T1 first;
    private T2 second;

    /**
     * Constructs a new mutable pair with the specified elements.
     *
     * @param first the first element
     * @param second the second element
     */
    public JXPair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Static factory method to create a new {@code JXPair} instance.
     */
    public static <T1, T2> JXPair<T1, T2> of(T1 first, T2 second) {
        return new JXPair<>(first, second);
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

    /**
     * Swaps the elements of the pair and returns a new mutable pair.
     *
     * @return a new {@code JXPair} with the first and second elements swapped
     */
    public JXPair<T2, T1> swap() {
        return new JXPair<>(second, first);
    }

    /**
     * Applies mapping functions to both elements of this tuple, producing a new mutable tuple.
     */
    public <R1, R2> JXPair<R1, R2> map(Function<? super T1, ? extends R1> mapper1, Function<? super T2, ? extends R2> mapper2) {
        return new JXPair<>(mapper1.apply(first), mapper2.apply(second));
    }

    /**
     * Applies a mapping function to the first element, keeping the second element unchanged.
     */
    public <R> JXPair<R, T2> mapFirst(Function<? super T1, ? extends R> mapper) {
        return new JXPair<>(mapper.apply(first), second);
    }

    /**
     * Applies a mapping function to the second element, keeping the first element unchanged.
     */
    public <R> JXPair<T1, R> mapSecond(Function<? super T2, ? extends R> mapper) {
        return new JXPair<>(first, mapper.apply(second));
    }

    /**
     * Creates a new immutable {@link ImmutablePair} from the elements of this tuple.
     *
     * @return a new immutable tuple with the same elements.
     */
    public ImmutablePair<T1, T2> toImmutable() {
        return new ImmutablePair<>(first, second);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> that)) return false;
        return Objects.equals(first, that.first()) && Objects.equals(second, that.second());
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}