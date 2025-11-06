package org.javaxtend.data;

import org.javaxtend.validation.Guard;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a DataFrame that has been grouped by one or more columns.
 * <p>
 * This is an intermediate object returned by {@link DataFrame#groupBy(String...)}.
 * It holds the grouped data and provides methods to perform aggregations (e.g., sum, mean, count)
 * on the groups.
 */
public class GroupedDataFrame {

    private final DataFrame sourceDf;
    private final List<String> groupingColumns;
    private Map<List<Object>, List<Object>> groups;

    /**
     * Internal constructor. Instances are created via {@link DataFrame#groupBy(String...)}.
     *
     * @param sourceDf        The original DataFrame.
     * @param groupingColumns The names of the columns to group by.
     */
    GroupedDataFrame(DataFrame sourceDf, String... groupingColumns) {
        this.sourceDf = sourceDf;
        this.groupingColumns = List.of(groupingColumns);
    }

    /**
     * Lazily builds the groups by iterating through the source DataFrame once.
     */
    private void buildGroups() {
        if (this.groups != null) {
            return;
        }

        this.groups = new LinkedHashMap<>();
        for (int i = 0; i < sourceDf.size(); i++) {
            List<Object> key = new ArrayList<>();
            for (String colName : groupingColumns) {
                key.add(sourceDf.get(colName).getByPosition(i));
            }

            Object rowIndexLabel = sourceDf.index().get(i);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(rowIndexLabel);
        }
    }

    /**
     * Performs an aggregation on a specified column for each group.
     *
     * @param targetColumn The name of the column to aggregate.
     * @param aggFunc      The aggregation function to apply (e.g., "sum", "mean", "count").
     * @return A new DataFrame with the aggregated results.
     */
    public DataFrame agg(String targetColumn, String aggFunc) {
        Guard.against().nullOrBlank(targetColumn, "targetColumn");
        Guard.against().nullOrBlank(aggFunc, "aggFunc");

        buildGroups();

        final List<Object> newIndexLabels = new ArrayList<>();
        final List<Object> newValues = new ArrayList<>();

        for (Map.Entry<List<Object>, List<Object>> groupEntry : groups.entrySet()) {
            final List<Object> groupKey = groupEntry.getKey();
            final List<Object> groupRowLabels = groupEntry.getValue();

            final Series<?> targetSeries = sourceDf.get(targetColumn);
            final List<Object> groupValues = groupRowLabels.stream()
                    .map(targetSeries::getByLabel)
                    .collect(Collectors.toList());

            final Series<Object> groupSeries = new Series<>(groupValues);

            final Object aggregatedValue = switch (aggFunc.toLowerCase()) {
                case "sum" -> groupSeries.sum();
                case "mean" -> groupSeries.mean();
                case "count" -> (long) groupSeries.size();
                case "min" -> groupSeries.min().orElse(null);
                case "max" -> groupSeries.max().orElse(null);
                default -> throw new IllegalArgumentException("Unsupported aggregation function: " + aggFunc);
            };

            newIndexLabels.add(groupKey.getFirst());
            newValues.add(aggregatedValue);
        }

        String newColumnName = targetColumn + "_" + aggFunc;
        return DataFrame.createFromRawData(Map.of(newColumnName, newValues), newIndexLabels, null);
    }

    /**
     * Calculates the sum for a numeric column within each group.
     * @param targetColumn The column to sum.
     * @return A new DataFrame with the summed results.
     */
    public DataFrame sum(String targetColumn) {
        return agg(targetColumn, "sum");
    }

    /**
     * Calculates the mean (average) for a numeric column within each group.
     * @param targetColumn The column to average.
     * @return A new DataFrame with the mean results.
     */
    public DataFrame mean(String targetColumn) {
        return agg(targetColumn, "mean");
    }
}