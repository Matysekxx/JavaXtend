package org.javaxtend.benchmark;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple utility for running and comparing the performance of code blocks.
 * <p>
 * It provides a fluent API to define a series of tasks, run them (with an optional warm-up phase),
 * and display the results in a formatted table.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * new Benchmark("My Benchmark")
 *     .withWarmup(1_000_000)
 *     .addTask("Task A", () -> {
 *         // ... code for task A ...
 *     })
 *     .addTask("Task B", () -> {
 *         // ... code for task B ...
 *     })
 *     .withRuns(10) // Run each task 10 times
 *     .run()
 *     .print();
 * }</pre></blockquote>
 */
public class Benchmark {

    private final String title;
    private final Map<String, Runnable> tasks = new LinkedHashMap<>();
    private final List<Runnable> warmupTasks = new ArrayList<>();
    private int runsPerTask = 1;
    private int warmupRunsPerTask = 0;
    private boolean forceGc = false;

    /**
     * Constructs a new benchmark with a given title.
     * @param title The title to be displayed above the results table.
     */
    public Benchmark(String title) {
        this.title = title;
    }

    /**
     * Adds a task to be benchmarked.
     * @param name The name of the task.
     * @param task The code to be executed.
     * @return This {@code Benchmark} instance for chaining.
     */
    public Benchmark addTask(String name, Runnable task) {
        tasks.put(name, task);
        return this;
    }

    /**
     * Adds a task to be run during the warm-up phase.
     * This helps the JIT compiler to optimize the code before actual measurement.
     * @param task The warm-up task.
     * @return This {@code Benchmark} instance for chaining.
     */
    public Benchmark withWarmup(Runnable task) {
        warmupTasks.add(task);
        return this;
    }

    /**
     * Sets the number of times each task should be run.
     * The results will show statistics (avg, min, max) over these runs.
     * @param runs The number of runs for each task (must be at least 1).
     * @return This {@code Benchmark} instance for chaining.
     */
    public Benchmark withRuns(int runs) {
        if (runs < 1) {
            throw new IllegalArgumentException("Number of runs must be at least 1.");
        }
        this.runsPerTask = runs;
        return this;
    }

    /**
     * Sets the number of warm-up runs to perform for each task before measurement begins.
     * These runs are not included in the final statistics.
     * @param runs The number of warm-up runs for each task.
     * @return This {@code Benchmark} instance for chaining.
     */
    public Benchmark withWarmupRuns(int runs) {
        if (runs < 0) {
            throw new IllegalArgumentException("Number of warm-up runs cannot be negative.");
        }
        this.warmupRunsPerTask = runs;
        return this;
    }

    /**
     * Specifies whether to request a garbage collection run before each task.
     * <p>
     * This can help produce more stable results by reducing the chance of GC pauses during measurement.
     * @param forceGc {@code true} to request GC before each task's runs.
     * @return This {@code Benchmark} instance for chaining.
     */
    public Benchmark withForcedGc(boolean forceGc) {
        this.forceGc = forceGc;
        return this;
    }

    /**
     * Runs all added tasks, measures their execution time, and returns a report object.
     *
     * @return A {@link BenchmarkReport} containing the results of the run.
     */
    public BenchmarkReport run() {
        if (!warmupTasks.isEmpty()) {
            System.out.println("Warming up JIT compiler...");
            warmupTasks.forEach(Runnable::run);
            System.out.println("Warm-up complete. Starting benchmarks...\n");
        }

        List<TaskResult> results = new ArrayList<>();

        for (Map.Entry<String, Runnable> entry : tasks.entrySet()) {
            try {
                java.util.LongSummaryStatistics stats = new java.util.LongSummaryStatistics();

                if (forceGc) {
                    System.gc();
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                }

                for (int i = 0; i < warmupRunsPerTask; i++) { entry.getValue().run(); }

                for (int i = 0; i < runsPerTask; i++) {
                    long start = System.nanoTime();
                    entry.getValue().run();
                    long end = System.nanoTime();
                    stats.accept((end - start) / 1_000_000);
                }
                results.add(new TaskResult.Success(entry.getKey(), stats));
            } catch (Exception e) {
                results.add(new TaskResult.Failure(entry.getKey(), e));
            }
        }
        return new BenchmarkReport(title, runsPerTask, results);
    }
}