package org.javaxtend.async;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A fluent utility for automatically retrying asynchronous operations that may fail.
 *
 * <p>This class integrates seamlessly with {@link org.javaxtend.async.Async} and provides a simple,
 * functional API for defining retry strategies. It is particularly useful for transient failures
 * such as network timeouts or temporary unavailability of external systems.
 *
 * <p>Each retry attempt executes the given asynchronous supplier and, if it fails,
 * schedules another attempt after the specified delay — up to a configurable maximum number of attempts.
 *
 * <h2>Core Features</h2>
 * <ul>
 *   <li><b>Fluent configuration:</b> Customize retry count, delay, and retry conditions via builder-style methods.</li>
 *   <li><b>Type-safe retry conditions:</b> Retry only on specific exception types using {@link #retryOn(Class)}.</li>
 *   <li><b>Non-blocking scheduling:</b> Retries are handled on a lightweight daemon thread using a shared {@link ScheduledExecutorService}.</li>
 *   <li><b>Seamless Async integration:</b> Returns an {@link org.javaxtend.async.Async} that can be further composed with map/flatMap/recover/etc.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // An operation that might fail
 * Supplier<Async<String>> fetchApiData = () -> Async.of(() -> {
 *     if (Math.random() > 0.2) { // 80% chance of failure
 *         throw new java.io.IOException("Network is temporarily down");
 *     }
 *     return "API Data";
 * });
 *
 * // Configure and execute the retry strategy
 * Async<String> resilientFetch = AsyncRetry.of(fetchApiData)
 *     .maxAttempts(5)                          // up to 5 attempts
 *     .delay(Duration.ofMillis(200))           // wait 200ms between attempts
 *     .retryOn(java.io.IOException.class)      // retry only on I/O-related failures
 *     .run();
 *
 * // Safely await the result
 * org.javaxtend.functional.Try<String> result = resilientFetch.awaitAsTry();
 * result.ifSuccess(System.out::println)
 *       .ifFailure(ex -> System.err.println("Failed after all retries: " + ex.getMessage()));
 * }</pre>
 *
 * <h2>Behavior Summary</h2>
 * <ul>
 *   <li>{@link #maxAttempts(int)} defines the total number of allowed tries (including the first one).</li>
 *   <li>{@link #delay(Duration)} sets the pause between retry attempts.</li>
 *   <li>{@link #retryOn(Class)} restricts retries to specific exception types — other exceptions will stop immediately.</li>
 *   <li>{@link #run()} initiates the retry loop and returns an {@link org.javaxtend.async.Async} representing the entire process.</li>
 *   <li>Once all attempts fail, the returned future completes exceptionally with the last encountered error.</li>
 * </ul>
 *
 * <h2>Threading Model</h2>
 * <p>Retry delays are scheduled using a single-threaded daemon {@link ScheduledExecutorService},
 * ensuring that retry scheduling is lightweight and does not block calling threads.
 * Each actual operation runs asynchronously via the underlying {@code Async} supplier.</p>
 *
 * @param <T> the result type of the asynchronous operation
 */
public class AsyncRetry<T> {

    /**
     * A single-threaded daemon scheduler used for retry delays.
     */
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AsyncRetry-Scheduler");
        t.setDaemon(true);
        return t;
    });

    private final Supplier<Async<T>> operation;
    private int maxAttempts = 3;
    private Duration delay = Duration.ofMillis(100);
    private Predicate<Throwable> retryCondition = ex -> true;

    private AsyncRetry(Supplier<Async<T>> operation) {
        this.operation = operation;
    }

    /**
     * Creates a new {@code AsyncRetry} instance for the given asynchronous operation.
     *
     * @param operation the asynchronous supplier that produces an {@link Async} computation
     * @param <T>       the type of the computation result
     * @return a new {@code AsyncRetry} instance
     */
    public static <T> AsyncRetry<T> of(Supplier<Async<T>> operation) {
        return new AsyncRetry<>(operation);
    }

    /**
     * Sets the maximum number of retry attempts (including the initial one).
     *
     * @param maxAttempts the maximum number of attempts; must be >= 1
     * @return this {@code AsyncRetry} instance for chaining
     * @throws IllegalArgumentException if {@code maxAttempts < 1}
     */
    public AsyncRetry<T> maxAttempts(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Max attempts must be at least 1.");
        }
        this.maxAttempts = maxAttempts;
        return this;
    }

    /**
     * Sets the delay between retry attempts.
     *
     * @param delay the duration to wait before each retry
     * @return this {@code AsyncRetry} instance for chaining
     */
    public AsyncRetry<T> delay(Duration delay) {
        this.delay = delay;
        return this;
    }

    /**
     * Configures this retry to only retry when the thrown exception is an instance
     * of the given exception type.
     *
     * @param exceptionType the exception class that triggers a retry
     * @return this {@code AsyncRetry} instance for chaining
     */
    public AsyncRetry<T> retryOn(Class<? extends Throwable> exceptionType) {
        this.retryCondition = exceptionType::isInstance;
        return this;
    }

    /**
     * Executes the asynchronous operation with retry logic applied.
     * The resulting {@link Async} completes successfully once the operation succeeds,
     * or exceptionally after all retry attempts are exhausted.
     *
     * @return an {@link Async} representing the entire retry process
     */
    public Async<T> run() {
        CompletableFuture<T> finalFuture = new CompletableFuture<>();
        runAttempt(1, finalFuture, null);
        return Async.from(finalFuture);
    }

    /**
     * Executes one retry attempt and schedules the next if needed.
     */
    private void runAttempt(int attempt, CompletableFuture<T> finalFuture, Throwable lastException) {
        if (attempt > maxAttempts) {
            finalFuture.completeExceptionally(lastException);
            return;
        }

        operation.get().toCompletableFuture().whenComplete((result, ex) -> {
            if (ex == null) {
                finalFuture.complete(result);
            } else {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                if (retryCondition.test(cause) && attempt < maxAttempts) {
                    SCHEDULER.schedule(() ->
                                    runAttempt(attempt + 1, finalFuture, cause),
                            delay.toMillis(), TimeUnit.MILLISECONDS);
                } else {
                    finalFuture.completeExceptionally(cause);
                }
            }
        });
    }
}
