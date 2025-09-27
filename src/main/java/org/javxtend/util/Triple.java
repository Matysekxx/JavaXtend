package org.javxtend.util;

import java.util.List;
import java.util.Objects;
import org.javxtend.util.function.TriFunction;

/**
 *
 * Defines a contract for a group of three elements, commonly known as a 3-tuple or a triple.
 * <p>
 * This serves as a generic container for three objects, which may be of different types.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 *
 * @see JXTriple
 * @see ImmutableTriple
 */
public interface Triple<T1, T2, T3> {

    T1 getFirst();

    T2 getSecond();

    T3 getThird();

    /**
     * Checks if the triple is empty, which is true if all elements are null.
     * @return {@code true} if all elements are null, {@code false} otherwise
     */
    default boolean isEmpty() {
        return getFirst() == null && getSecond() == null && getThird() == null;
    }

    /**
     * Checks if the triple contains the specified value in any of its elements.
     * @param value the value to check for
     * @return {@code true} if the value is found, {@code false} otherwise
     */
    default boolean contains(Object value) {
        return Objects.equals(getFirst(), value) || Objects.equals(getSecond(), value) || Objects.equals(getThird(), value);
    }

    /**
     * Applies a function to the elements of this triple.
     * <p>
     * This allows for a form of destructuring, where the elements of the triple
     * are passed as arguments to the provided function.
     *
     * @param <R> the type of the result
     * @param function the function to apply
     * @return the result of applying the function to the triple's elements
     */
    default <R> R apply(TriFunction<? super T1, ? super T2, ? super T3, ? extends R> function) {
        return function.apply(getFirst(), getSecond(), getThird());
    }

    List<Object> toList();

    Object[] toArray();
}