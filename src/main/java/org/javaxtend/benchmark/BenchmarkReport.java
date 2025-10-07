package org.javaxtend.benchmark;

import org.javaxtend.console.ConsoleColors;
import org.javaxtend.console.ConsoleTable;
import org.javaxtend.io.IO;

import java.util.List;

/**
 * Represents the results of a benchmark run.
 * <p>
 * This class holds the statistics for all executed tasks and provides
 * methods to display them.
 */
public record BenchmarkReport(String title, int runsPerTask, List<TaskResult> results) {

    /**
     * Prints the benchmark results to the console in a formatted table.
     */
    public void print() {
        ConsoleTable resultsTable = new ConsoleTable().addRow(
                ConsoleColors.YELLOW.colorize(title + " (" + runsPerTask + " runs)"),
                ConsoleColors.YELLOW.colorize("Avg (ms)"),
                ConsoleColors.YELLOW.colorize("Min (ms)"),
                ConsoleColors.YELLOW.colorize("Max (ms)")
        );

        for (TaskResult result : results) {
            if (result instanceof TaskResult.Success success) {
                resultsTable.addRow(result.taskName(),
                        String.format("%.2f", success.stats().getAverage()),
                        String.valueOf(success.stats().getMin()),
                        String.valueOf(success.stats().getMax()));
            } else if (result instanceof TaskResult.Failure failure) {
                String failedMessage = ConsoleColors.RED.colorize("FAILED");
                resultsTable.addRow(result.taskName(), failedMessage, "-", "-");
                IO.println(ConsoleColors.RED.colorize(
                        "\nBenchmark task '" + result.taskName() + "' failed: " + failure.exception().getMessage()));
                failure.exception().printStackTrace(System.err);
                IO.println("");
            }
        }
        resultsTable.print();
    }
}