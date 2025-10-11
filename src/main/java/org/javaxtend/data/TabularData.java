package org.javaxtend.data;

import java.util.List;

/**
 * Represents a generic, table-like data structure with an index.
 * This interface defines the common behavior for structures like {@link Series} and a potential DataFrame.
 *
 * @param <S> The concrete type of the implementing class, used for fluent method chaining.
 */
public interface TabularData<R, S extends TabularData<R, S>> {

    /**
     * Returns the number of rows in the data structure.
     * @return The number of rows.
     */
    int size();

    /**
     * Returns the index (row labels) of the data structure.
     * @return An immutable list of index labels.
     */
    List<?> index();


    /**
     * Returns the row/value for a specific index label.
     *
     * @param label The index label of the value to retrieve.
     * @return The row/value corresponding to the given label.
     * @throws KeyException if the label is not found in the index.
     */
    R getByLabel(Object label);

    /**
     * Returns the row/value at a specific integer position.
     *
     * @param position The integer position of the value to retrieve.
     * @return The row/value at the specified position.
     * @throws IndexOutOfBoundsException if the position is out of range.
     */
    R getByPosition(int position);

    /**
     * Returns a new data structure containing a slice of the original.
     *
     * @param start The starting position (inclusive).
     * @param end   The ending position (exclusive).
     * @return A new data structure representing the specified slice.
     */
    S slice(int start, int end);

    /**
     * Returns the first {@code n} rows of the data structure.
     * @param n The number of rows to return.
     * @return A new instance of the data structure containing the first n rows.
     */
    S head(int n);
}