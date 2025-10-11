package org.javaxtend.data;

import org.javaxtend.validation.Guard;

import java.util.*;

/**
 * An immutable, ordered sequence of labels used for indexing data structures like {@link Series}.
 * <p>
 * This class is optimized for fast lookups by mapping labels to their integer positions.
 * It ensures that the underlying data is immutable.
 */
public final class Index implements Iterable<Object> {

    private final List<Object> labels;
    private final Map<Object, Integer> labelToPosition;

    /**
     * Constructs an Index from a list of labels.
     *
     * @param labels The list of labels. Must not be null.
     */
    public Index(List<?> labels) {
        Guard.against().isNull(labels, "Labels cannot be null.");
        this.labels = List.copyOf(labels);
        this.labelToPosition = buildIndexMap();
    }

    private Map<Object, Integer> buildIndexMap() {
        final var map = new HashMap<Object, Integer>(labels.size());
        for (int i = 0; i < labels.size(); i++) {
            map.put(labels.get(i), i);
        }
        return Map.copyOf(map);
    }

    /**
     * Returns the number of labels in the index.
     * @return The size of the index.
     */
    public int size() {
        return labels.size();
    }

    /**
     * Returns the integer position of a given label.
     *
     * @param label The label to find.
     * @return The integer position of the label.
     * @throws KeyException if the label is not found.
     */
    public int getPosition(Object label) {
        final Integer pos = labelToPosition.get(label);
        if (pos == null) {
            throw new KeyException("Label not found in index: " + label);
        }
        return pos;
    }

    /**
     * Returns the underlying immutable list of labels.
     * @return A list of labels.
     */
    public List<Object> toList() {
        return labels;
    }

    @Override
    public Iterator<Object> iterator() {
        return labels.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return labels.equals(index.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labels);
    }
}