package org.javaxtend.benchmark;

import java.util.LongSummaryStatistics;

/**
 * Represents the outcome of a single benchmarked task.
 * This is a sealed interface, allowing for either a Success or a Failure.
 */
public sealed interface TaskResult {
    String taskName();

    /** A successful task result, containing performance statistics. */
    record Success(String taskName, LongSummaryStatistics stats) implements TaskResult {}

    /** A failed task result, containing the exception that occurred. */
    record Failure(String taskName, Exception exception) implements TaskResult {}
}