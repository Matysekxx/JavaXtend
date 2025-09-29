package org.javxtend.util;

import java.util.List;
import java.util.Objects;

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

    List<Object> toList();

    Object[] toArray();
}