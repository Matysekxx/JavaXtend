package org.javaxtend.util;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Defines a contract for a pair of elements.
 * <p>
 * A pair is a generic container for two objects, which may be of different types.
 * It is primarily used as a convenient way for methods to return multiple values
 * without creating a dedicated class for that purpose.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 *
 * @see JXPair
 */
public interface Pair<T1, T2> {
    T1 first();
    
    T2 second();

    /**
     * Checks if the pair is empty, which is true if both elements are null.
     * @return {@code true} if both elements are null, {@code false} otherwise
     */
    default boolean isEmpty() {
        return first() == null && second() == null;
    }
    
    /**
     * Checks if the pair contains the specified value in either of its elements.
     * @param value the value to check for
     * @return {@code true} if the value is found, {@code false} otherwise
     */
    default boolean contains(Object value) {
        return Objects.equals(first(), value) || Objects.equals(second(), value);
    }

    /**
     * Applies a function to the elements of this pair.
     * <p>
     * This allows for a form of destructuring, where the elements of the pair
     * are passed as arguments to the provided function.
     *
     * @param <R> the type of the result
     * @param function the function to apply
     * @return the result of applying the function to the pair's elements
     */
    default <R> R apply(BiFunction<? super T1, ? super T2, ? extends R> function) {
        return function.apply(first(), second());
    }

    Object[] toArray();
    
    List<Object> toList();
}
