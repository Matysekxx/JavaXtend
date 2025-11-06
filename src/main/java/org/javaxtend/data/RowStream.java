package org.javaxtend.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A stream-like object for processing rows of a DataFrame in a fluent manner.
 * <p>
 * This class is returned by {@link DataFrame#rows()} and allows for operations like
 * {@link #filter(Predicate)} to be chained. The final result can be collected
 * back into a new DataFrame using {@link #toDataFrame()}.
 */
public class RowStream {

    private final Stream<Row> stream;
    private final DataFrame sourceDf;

    RowStream(Stream<Row> stream, DataFrame sourceDf) {
        this.stream = stream;
        this.sourceDf = sourceDf;
    }

    /**
     * Filters the rows in the stream based on a predicate.
     * @param predicate The predicate to apply to each row.
     * @return A new RowStream containing only the filtered rows.
     */
    public RowStream filter(Predicate<Row> predicate) {
        return new RowStream(stream.filter(predicate), sourceDf);
    }

    /**
     * Collects the rows in the stream into a new DataFrame.
     * @return A new DataFrame containing the rows from this stream.
     */
    public DataFrame toDataFrame() {
        List<Row> filteredRows = stream.toList();

        if (filteredRows.isEmpty()) {
            return new DataFrame();
        }

        List<Object> newIndexLabels = filteredRows.stream().map(Row::getLabel).toList();
        Map<String, List<Object>> columnData = sourceDf.columns().stream()
                .collect(Collectors.toMap(Function.identity(), colName ->
                                filteredRows.stream().map(row -> row.get(colName)).toList()
                        , (v1, v2) -> v1, LinkedHashMap::new));

        return DataFrame.createFromRawData(new LinkedHashMap<>(columnData), newIndexLabels, sourceDf.getName());
    }
}