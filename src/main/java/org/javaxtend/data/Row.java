package org.javaxtend.data;

import java.util.Map;

/**
 * Represents a single row in a DataFrame, providing type-safe accessors for column values.
 * <p>
 * An instance of this class is a wrapper around a {@code Map<String, Object>} that
 * represents the row's data. It offers convenience methods like {@link #getInt(String)}
 * and {@link #getString(String)} to avoid manual casting.
 */
public final class Row {

    private final Object label;
    final Map<String, Object> data;

    /**
     * Internal constructor. Instances are created by the DataFrame.
     * @param label The index label for this row.
     * @param data The map of column names to values for this row.
     */
    Row(Object label, Map<String, Object> data) {
        this.label = label;
        this.data = data;
    }

    /**
     * @return The index label of this row.
     */
    public Object getLabel() {
        return label;
    }

    /**
     * Gets the value of a column as a raw {@code Object}.
     * @param columnName The name of the column.
     * @return The value from the specified column.
     * @throws KeyException if the column does not exist in the row.
     */
    public Object get(String columnName) {
        if (!data.containsKey(columnName)) {
            throw new KeyException("Column not found in row: " + columnName);
        }
        return data.get(columnName);
    }

    /**
     * Gets the value of a column as a {@code String}.
     * @throws ClassCastException if the value is not a String.
     */
    public String getString(String columnName) {
        return (String) get(columnName);
    }

    /**
     * Gets the value of a column as an {@code Integer}.
     * This method can handle values that are any subclass of {@link Number}.
     * @throws ClassCastException if the value is not a number.
     */
    public Integer getInt(String columnName) {
        Object value = get(columnName);
        if (value instanceof Number num) {
            return num.intValue();
        }
        return (Integer) value;
    }

    /**
     * Gets the value of a column as a {@code Double}.
     * This method can handle values that are any subclass of {@link Number}.
     * @throws ClassCastException if the value is not a number.
     */
    public Double getDouble(String columnName) {
        Object value = get(columnName);
        if (value instanceof Number num) {
            return num.doubleValue();
        }
        return (Double) value;
    }

    @Override
    public String toString() {
        return "Row(label=" + label + ", data=" + data + ")";
    }
}