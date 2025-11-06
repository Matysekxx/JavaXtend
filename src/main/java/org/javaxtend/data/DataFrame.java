package org.javaxtend.data;

import org.javaxtend.console.ConsoleTable;
import org.javaxtend.validation.Guard;
import org.javaxtend.util.IntRange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An immutable, two-dimensional, labeled data structure with columns of potentially different types.
 * It is conceptually similar to a spreadsheet, a SQL table, or a dictionary of {@link Series} objects.
 * <p>
 * A DataFrame consists of three main components:
 * <ul>
 *     <li><b>columns:</b> A map where keys are column names (String) and values are {@link Series} objects.</li>
 *     <li><b>rowIndex:</b> A shared, immutable {@link Index} for all rows.</li>
 *     <li><b>name:</b> An optional name for the DataFrame.</li>
 * </ul>
 * Operations that modify the DataFrame, such as adding a column or filtering rows, return a new DataFrame instance.
 */
public class DataFrame implements TabularData<Map<String, Object>, DataFrame> {

    private final Index rowIndex;
    private final Map<String, Series<?>> columns;
    private final String name;

    /**
     * Constructs an empty DataFrame.
     */
    public DataFrame() {
        this.rowIndex = new Index(List.of());
        this.columns = Map.of();
        this.name = null;
    }

    /**
     * Constructs a DataFrame from a map of columns, a row index, and an optional name.
     * <p>
     * This is the primary constructor and performs validation to ensure data consistency.
     *
     * @param columns A map where keys are column names and values are {@link Series} objects.
     * @param index   The shared {@link Index} for the rows.
     * @param name    An optional name for the DataFrame.
     * @throws IllegalArgumentException if any {@link Series} has a different size than the provided {@code index}.
     */
    public DataFrame(Map<String, Series<?>> columns, Index index, String name) {
        Guard.against().isNull(columns, "Columns map cannot be null.");
        Guard.against().isNull(index, "Row index cannot be null.");

        final int expectedSize = index.size();
        for (Map.Entry<String, Series<?>> entry : columns.entrySet()) {
            final Series<?> series = entry.getValue();
            Guard.against().isNull(series, "Series for column '" + entry.getKey() + "' cannot be null.");
            if (series.size() != expectedSize) {
                throw new IllegalArgumentException(
                        "Column '" + entry.getKey() + "' has size " + series.size() + ", but expected " + expectedSize + "."
                );
            }
        }

        this.rowIndex = index;
        this.columns = new LinkedHashMap<>(columns);
        this.name = name;
    }

    /**
     * A convenience constructor that creates a DataFrame from a map of lists.
     * An integer index will be automatically generated.
     *
     * @param data A map where keys are column names and values are lists of column data.
     * @throws IllegalArgumentException if lists have different sizes.
     */
    public DataFrame(Map<String, List<?>> data) {
        Guard.against().isNull(data, "data map cannot be null.");

        if (data.isEmpty()) {
            this.rowIndex = new Index(List.of());
            this.columns = Map.of();
            this.name = null;
            return;
        }

        final int size = data.values().iterator().next().size();
        for (Map.Entry<String, List<?>> entry : data.entrySet()) {
            if (entry.getValue().size() != size) {
                throw new IllegalArgumentException(
                        "All lists in the map must have the same size. Column '" + entry.getKey() +
                        "' has size " + entry.getValue().size() + ", but expected " + size + "."
                );
            }
        }

        this.rowIndex = new Index(IntRange.of(0, size - 1).toList());
        final Map<String, Series<?>> newColumns = new LinkedHashMap<>();
        for (Map.Entry<String, List<?>> entry : data.entrySet()) {
            newColumns.put(entry.getKey(), new Series<>(entry.getValue(), this.rowIndex.toList(), entry.getKey()));
        }
        this.columns = newColumns;
        this.name = null;
    }

    /**
     * A convenience static factory method for creating a DataFrame from a map of lists.
     * An integer index will be automatically generated.
     *
     * @param data A map where keys are column names and values are lists of column data.
     * @return A new DataFrame instance.
     * @throws IllegalArgumentException if lists have different sizes.
     */
    public static DataFrame of(Map<String, List<?>> data) {
        return new DataFrame(data);
    }

    static DataFrame createFromRawData(Map<String, List<?>> columnData, List<?> indexLabels, String name) {
        final Index newIndex = new Index(indexLabels);
        final Map<String, Series<?>> newColumns = new LinkedHashMap<>();
        for (Map.Entry<String, List<?>> entry : columnData.entrySet()) {
            newColumns.put(entry.getKey(), new Series<>(entry.getValue(), newIndex.toList(), entry.getKey()));
        }
        return new DataFrame(newColumns, newIndex, name);
    }

    /**
     * Creates a DataFrame from a {@link ConsoleTable} instance.
     * <p>
     * This method assumes:
     * <ul>
     *     <li>The first row of the ConsoleTable is the header.</li>
     *     <li>The first column of the ConsoleTable is the index.</li>
     *     <li>All data is read as {@code String}.</li>
     * </ul>
     *
     * @param table The ConsoleTable to convert.
     * @return A new DataFrame instance.
     */
    public static DataFrame fromConsoleTable(ConsoleTable table) {
        Guard.against().isNull(table, "table");
        List<String[]> rows = table.getRows();

        if (rows.size() < 2) {
            return new DataFrame();
        }

        String[] header = rows.getFirst();
        List<String> columnNames = new ArrayList<>(List.of(header).subList(1, header.length));

        List<Object> indexLabels = new ArrayList<>();
        Map<String, List<Object>> columnData = columnNames.stream()
                .collect(Collectors.toMap(Function.identity(), name -> new ArrayList<>(), (v1, v2) -> v1, LinkedHashMap::new));

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 0) {
                indexLabels.add(row[0]);
                for (int j = 0; j < columnNames.size(); j++) {
                    columnData.get(columnNames.get(j)).add(row.length > j + 1 ? row[j + 1].trim() : null);
                }
            }
        }

        return createFromRawData(new LinkedHashMap<>(columnData), indexLabels, null);
    }

    @Override
    public int size() {
        return rowIndex.size();
    }

    @Override
    public List<?> index() {
        return rowIndex.toList();
    }

    @Override
    public Map<String, Object> getByLabel(Object label) {
        Guard.against().isNull(label, "Label cannot be null.");
        return getByPosition(rowIndex.getPosition(label));
    }

    /**
     * Returns a row at a specific integer position as a map.
     *
     * @param position The integer position of the row to retrieve.
     * @return An immutable map representing the row, where keys are column names.
     */
    @Override
    public Map<String, Object> getByPosition(int position) {
        final Map<String, Object> row = new LinkedHashMap<>();
        for (Map.Entry<String, Series<?>> entry : columns.entrySet())
            row.put(entry.getKey(), entry.getValue().getByPosition(position));
        return Map.copyOf(row);
    }

    /**
     * Returns an immutable list of the column names.
     * @return A list of column names.
     */
    public List<String> columns() {
        return List.copyOf(this.columns.keySet());
    }

    /**
     * Selects a single column and returns it as a {@link Series}.
     * @param columnName The name of the column to retrieve.
     * @return The {@link Series} corresponding to the column name.
     * @throws KeyException if the column name is not found.
     */
    public Series<?> get(String columnName) {
        Guard.against().isNull(columnName, "Column name cannot be null.");
        final Series<?> series = columns.get(columnName);
        if (series == null) {
            throw new KeyException("Column not found: " + columnName);
        }
        return series;
    }

    @Override
    public DataFrame slice(int start, int end) {
        Guard.against().outOfRange(start, 0, size(), "start");
        Guard.against().outOfRange(end, start, size(), "end");
        return new DataFrame(
                new LinkedHashMap<>() {{
                    columns.forEach((key, value) -> this.put(key, value.slice(start, end)));
                }},
                new Index(rowIndex.toList().subList(start, end)),
                this.name
        );
    }

    @Override
    public DataFrame head(int n) {
        if (n <= 0) {
            return new DataFrame();
        }
        final int limit = Math.min(n, size());
        return slice(0, limit);
    }

    /**
     * Returns a new DataFrame with a new column added or an existing one replaced.
     * The new column must have the same size as the DataFrame.
     *
     * <h2>Example of Usage:</h2>
     * <blockquote><pre>{@code
     * Series<Double> newAges = new Series<>(List.of(26.0, 31.0, 36.0), df.index());
     * DataFrame updatedDf = df.withColumn("age", newAges);
     * }</pre></blockquote>
     *
     * @param columnName The name of the column to add or replace.
     * @param columnSeries The {@link Series} containing the data for the new column.
     * @return A new DataFrame with the specified column.
     */
    public DataFrame withColumn(String columnName, Series<?> columnSeries) {
        Guard.against().isNull(columnName, "Column name cannot be null.");
        Guard.against().isNull(columnSeries, "Column series cannot be null.");

        if (columnSeries.size() != this.size()) {
            throw new IllegalArgumentException("New column must have the same size as the DataFrame.");
        }

        final Map<String, Series<?>> newColumns = new LinkedHashMap<>(this.columns);
        newColumns.put(columnName, columnSeries);

        return new DataFrame(newColumns, this.rowIndex, this.name);
    }

    /**
     * Returns a new DataFrame with the specified column removed.
     *
     * <h2>Example of Usage:</h2>
     * <blockquote><pre>{@code
     * DataFrame smallerDf = df.dropColumn("city");
     * }</pre></blockquote>
     *
     * @param columnName The name of the column to remove.
     * @return A new DataFrame without the specified column.
     * @throws KeyException if the column name is not found.
     */
    public DataFrame dropColumn(String columnName) {
        Guard.against().isNull(columnName, "Column name cannot be null.");
        if (!columns.containsKey(columnName)) {
            throw new KeyException("Column to drop not found: " + columnName);
        }

        Map<String, Series<?>> newColumns = this.columns.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(columnName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));

        return new DataFrame(newColumns, this.rowIndex, this.name);
    }

    /**
     * Filters rows of the DataFrame based on a predicate.
     * The predicate is applied to each row, which is represented as a {@code Map<String, Object>}.
     *
     * <h2>Example of Usage:</h2>
     * <blockquote><pre>{@code
     * // Select rows where age is greater than 30
     * DataFrame filteredDf = df.filter(row -> (Integer) row.get("age") > 30);
     * }</pre></blockquote>
     *
     * @param rowPredicate A predicate that returns true for rows that should be kept.
     * @return A new DataFrame containing only the rows that satisfy the predicate.
     */
    public DataFrame filter(Predicate<Row> rowPredicate) {
        Guard.against().isNull(rowPredicate, "Row predicate cannot be null.");
        return this.rows().filter(rowPredicate).toDataFrame();
    }

    /**
     * Saves the DataFrame to a CSV file.
     * <p>
     * The first line will be the header (column names), followed by data rows.
     *
     * @param filePath The path to the output CSV file.
     * @throws IOException if an I/O error occurs while writing the file.
     */
    public void toCSV(String filePath) throws IOException {
        Guard.against().nullOrBlank(filePath, "filePath");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(String.join(",", this.columns()));
            writer.newLine();
            for (int i = 0; i < this.size(); i++) {
                final List<String> rowValues = new ArrayList<>();
                for (String colName : this.columns()) {
                    rowValues.add(String.valueOf(this.get(colName).getByPosition(i)));
                }
                writer.write(String.join(",", rowValues));
                writer.newLine();
            }
        }
    }

    /**
     * Creates a DataFrame by reading a CSV file.
     * <p>
     * This method assumes:
     * <ul>
     *     <li>The first line of the file is the header row containing column names.</li>
     *     <li>The delimiter is a comma (',').</li>
     *     <li>All data is read as {@code String}.</li>
     *     <li>A default integer index (0, 1, 2, ...) is generated for the rows.</li>
     * </ul>
     *
     * <h2>Example of Usage:</h2>
     * <pre>{@code
     * DataFrame df = DataFrame.fromCSV("data/users.csv");
     * System.out.println(df.head(5));
     * }</pre>
     *
     * @param filePath The path to the CSV file.
     * @return A new DataFrame containing the data from the CSV file.
     * @throws IOException if an I/O error occurs while reading the file.
     * @throws IllegalArgumentException if the file is empty or contains inconsistent row lengths.
     */
    public static DataFrame fromCSV(String filePath) throws IOException {
        Guard.against().nullOrBlank(filePath, "filePath");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file is empty or contains no header.");
            }

            List<String> columnNames = List.of(headerLine.split(","));
            Map<String, List<String>> columnData = columnNames.stream()
                    .collect(Collectors.toMap(Function.identity(), name -> new ArrayList<>(), (v1, v2) -> v1, LinkedHashMap::new));

            String line;
            int rowCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != columnNames.size()) {
                    throw new IllegalArgumentException("CSV file has inconsistent number of columns at row " + (rowCount + 1));
                }
                for (int i = 0; i < columnNames.size(); i++) {
                    columnData.get(columnNames.get(i)).add(values[i]);
                }
                rowCount++;
            }

            List<Integer> rowIndexLabels = IntRange.of(0, rowCount - 1).toList();
            return createFromRawData(new LinkedHashMap<>(columnData), rowIndexLabels, filePath);
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        if (name != null && !name.isBlank()) {
            result.append("DataFrame: ").append(name).append("\n");
        }
        result.append(ConsoleTable.fromDataFrame(this).render());
        if (size() > 20) {
            result.append("\n... (").append(size() - 20).append(" more rows)");
        }
        return result.toString();
    }


    /**
     * Returns a stream of rows for fluent, type-safe processing.
     * @return A {@link RowStream} instance.
     */
    public RowStream rows() {
        final Stream<Row> rowStream = IntStream.range(0, size()).mapToObj(i -> {
            Object label = index().get(i);
            Map<String, Object> data = getByPosition(i);
            return new Row(label, data);
        });
        return new RowStream(rowStream, this);
    }

    /**
     * Groups the DataFrame by one or more columns.
     * <p>
     * This is the entry point for performing split-apply-combine operations.
     * The returned {@link GroupedDataFrame} object can then be used to apply aggregation functions.
     *
     * @param columnNames The names of the columns to group by.
     * @return A {@code GroupedDataFrame} instance ready for aggregation.
     */
    public GroupedDataFrame groupBy(String... columnNames) {
        for (String colName : columnNames) {
            Guard.against().isFalse(this.columns.containsKey(colName), "Grouping column not found: " + colName);
        }
        return new GroupedDataFrame(this, columnNames);
    }
    
    String getName() {
        return name;
    }
    
    Index getIndex() {
        return rowIndex;
    }
    
    
}
