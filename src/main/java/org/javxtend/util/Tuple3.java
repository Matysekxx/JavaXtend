package org.javxtend.util;

import java.util.List;

/**
 * Defines a contract for a group of three elements, commonly known as a 3-tuple or a triple.
 * <p>
 * This serves as a generic container for three objects, which may be of different types.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 *
 * @see JXTuple3
 * @see ImmutableTuple3
 */
public interface Tuple3<T1, T2, T3> {

    T1 getFirst();

    T2 getSecond();

    T3 getThird();

    /**
     * Checks if the tuple is empty, which is true if all elements are null.
     * @return {@code true} if all elements are null, {@code false} otherwise
     */
    default boolean isEmpty() {
        return getFirst() == null && getSecond() == null && getThird() == null;
    }

    List<Object> toList();
}