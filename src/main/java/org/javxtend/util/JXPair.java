package org.javxtend.util;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A mutable, general-purpose implementation of the {@link Pair} interface.
 * <p>
 * This class serves as a standard container for a pair of objects and is the
 * primary implementation for most use cases.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Create a pair to hold a user's name and age
 *     Pair&lt;String, Integer&gt; userProfile = JXPair.of("John Doe", 30);
 *
 *     // Access the elements
 *     String name = userProfile.getFirst();
 *     Integer age = userProfile.getSecond();
 *
 *     // Prints: John Doe is 30 years old.
 *     System.out.println(name + " is " + age + " years old.");
 * </pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 */
public class JXPair<T1, T2> implements Pair<T1, T2> {

    private T1 first;
    private T2 second;

    /**
     * Constructs a new tuple with the specified elements.
     *
     * @param first the first element
     * @param second the second element
     */
    public JXPair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Constructs a new pair by copying the elements from another pair.
     *
     * @param pair the pair to copy elements from
     */
    public JXPair(Pair<T1, T2> pair) {
        this.first = pair.getFirst();
        this.second = pair.getSecond();
    }

    /**
     * Static factory method to create a new {@code JXPair} instance.
     *
     * @param first the first element
     * @param second the second element
     * @param <T1> the type of the first element
     * @param <T2> the type of the second element
     * @return a new {@code JXPair} containing the provided elements
     */
    public static <T1, T2> JXPair<T1, T2> of(T1 first, T2 second) {
        return new JXPair<>(first, second);
    }

    @Override
    public T2 getSecond() {
        return second;
    }

    @Override
    public T1 getFirst() {
        return first;
    }
    
    public void setFirst(T1 first) {
        this.first = first;
    }
    
    public void setSecond(T2 second) {
        this.second = second;
    }

    /**
     * Swaps the elements of the pair and returns a new pair with the swapped elements.
     *
     * @return a new {@code JXPair} with the first and second elements swapped
     */
    public JXPair<T2, T1> swap() {
        return new JXPair<>(second, first);
    }

    /**
     * Applies mapping functions to both elements of this tuple, producing a new tuple.
     *
     * @param mapper1 a function to apply to the first element
     * @param mapper2 a function to apply to the second element
     * @param <R1> the type of the first element of the new tuple
     * @param <R2> the type of the second element of the new tuple
     * @return a new {@code JXPair} with the transformed elements
     */
    public <R1, R2> JXPair<R1, R2> map(Function<? super T1, ? extends R1> mapper1, Function<? super T2, ? extends R2> mapper2) {
        return new JXPair<>(mapper1.apply(first), mapper2.apply(second));
    }

    /**
     * Applies a mapping function to the first element, keeping the second element unchanged.
     *
     * @param mapper a function to apply to the first element
     * @param <R> the new type of the first element
     * @return a new {@code JXPair} with the transformed first element.
     */
    public <R> JXPair<R, T2> mapFirst(Function<? super T1, ? extends R> mapper) {
        return new JXPair<>(mapper.apply(first), second);
    }

    /**
     * Applies a mapping function to the second element, keeping the first element unchanged.
     *
     * @param mapper a function to apply to the second element
     * @param <R> the new type of the second element
     * @return a new {@code JXPair} with the transformed second element.
     */
    public <R> JXPair<T1, R> mapSecond(Function<? super T2, ? extends R> mapper) {
        return new JXPair<>(first, mapper.apply(second));
    }

    /**
     * Creates a new {@link ImmutablePair} from the elements of this tuple.
     *
     * @return a new immutable tuple with the same elements.
     */
    public ImmutablePair<T1, T2> toImmutable() {
        return new ImmutablePair<>(first, second);
    }

    /**
     * Returns an array containing the elements of this tuple.
     *
     * @return an array of size 2 with the first and second elements
     */
    @Override
    public Object[] toArray() {
        return new Object[] { first, second };
    }

    /**
     * Returns an unmodifiable list containing the elements of this tuple.
     *
     * @return an unmodifiable {@code List} with the first and second elements
     */
    @Override
    public List<Object> toList() {
        return List.of(first, second);
    }

    /**
     * Returns a string representation of the tuple in the format {@code (first, second)}.
     *
     * @return a string representation of the tuple
     */
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
        JXPair<?, ?> jxTuple = (JXPair<?, ?>) o;
        return Objects.equals(first, jxTuple.first) && Objects.equals(second, jxTuple.second);
    }
}
