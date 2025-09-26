package org.javxtend.util;

import java.util.List;

/**
 * Defines a contract for a pair of elements, commonly known as a 2-tuple.
 * <p>
 * A tuple is a generic container for two objects, which may be of different types.
 * It is primarily used as a convenient way for methods to return multiple values
 * without creating a dedicated class for that purpose.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 *
 * @see JXTuple
 * @see JXNumericTuple
 */
public interface Tuple<T1, T2> {
    T1 getFirst();
    
    T2 getSecond();

    /**
     * Checks if the tuple is empty, which is true if both elements are null.
     * @return {@code true} if both elements are null, {@code false} otherwise
     */
    default boolean isEmpty() {
        return getFirst() == null && getSecond() == null;
    }
    
    boolean contains(Object value);
    
    Object[] toArray();
    
    List<Object> toList();
}
