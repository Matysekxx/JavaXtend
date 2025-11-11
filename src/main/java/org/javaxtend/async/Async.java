package org.javaxtend.async;

import org.javaxtend.functional.Try;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.time.Duration;

/**
 * A lightweight wrapper around {@link CompletableFuture} designed for more expressive,
 * functional-style asynchronous programming.
 *
 * <p>This class enhances the standard {@link CompletableFuture} by providing:
 * <ul>
 *   <li>A clean, fluent API for composing async operations using {@code map} and {@code flatMap}</li>
 *   <li>Built-in recovery utilities ({@code recover}, {@code recoverWith}) for predictable error handling</li>
 *   <li>Timeout support and seamless integration with {@link org.javaxtend.functional.Try}</li>
 * </ul>
 *
 * The goal is to make asynchronous composition safer, clearer, and more readable —
 * avoiding nested try/catch blocks and reducing the verbosity typical of async code.
 *
 * <h2>Design Notes</h2>
 * <ul>
 *   <li>Internally relies on {@link CompletableFuture} without additional synchronization.</li>
 *   <li>All methods are non-mutating — each call produces a new {@code Async} instance.</li>
 *   <li>Blocking methods ({@link #await()}, {@link #awaitAsTry()}) are provided for convenience only
 *       and should not be used in event-loop or reactive environments.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * Async<String> fetchData = Async.of(() -> {
 *     if (Math.random() > 0.5) {
 *         return "Some data";
 *     } else {
 *         throw new RuntimeException("Network error");
 *     }
 * });
 *
 * Try<String> result = fetchData
 *     .map(String::toUpperCase)           // transform on success
 *     .recover("Default data")            // fallback if an exception occurs
 *     .withTimeout(1, TimeUnit.SECONDS)   // fail if not completed within 1s
 *     .awaitAsTry();                      // safely block and wrap into Try
 *
 * result.ifSuccess(System.out::println)
 *       .ifFailure(ex -> System.err.println("Operation failed: " + ex.getMessage()));
 * }</pre>
 *
 * <h2>Behavior Summary</h2>
 * <ul>
 *   <li>{@code recover(value)} returns the given value when an exception occurs.</li>
 *   <li>{@code recoverWith(fn)} derives a recovery value from the thrown exception.</li>
 *   <li>{@code withTimeout(...)} completes exceptionally with a
 *       {@link java.util.concurrent.TimeoutException} if not done in time.</li>
 *   <li>{@code await()} uses {@link CompletableFuture#join()} and throws
 *       {@link java.util.concurrent.CompletionException} on failure.</li>
 *   <li>{@code awaitAsTry()} wraps the blocking join in a {@link org.javaxtend.functional.Try},
 *       providing a safer, exception-free interface.</li>
 * </ul>
 *
 * @param <T> the type of the success value
 */

public class Async<T> {
    private final CompletableFuture<T> future;

    private Async(CompletableFuture<T> future) {
        this.future = future;
    }

    /**
     * Runs a task asynchronously on the common ForkJoinPool.
     *
     * @param supplier The task to run.
     * @param <T>      The type of the result.
     * @return A new {@code Async} instance representing the task.
     */
    public static <T> Async<T> of(Supplier<T> supplier) {
        return new Async<>(CompletableFuture.supplyAsync(supplier));
    }

    /**
     * Runs a task asynchronously using a specific executor.
     *
     * @param supplier The task to run.
     * @param executor The executor to use.
     * @param <T>      The type of the result.
     * @return A new {@code Async} instance representing the task.
     */
    public static <T> Async<T> of(Supplier<T> supplier, Executor executor) {
        return new Async<>(CompletableFuture.supplyAsync(supplier, executor));
    }

    /**
     * Wraps an existing CompletableFuture.
     *
     * @param future The future to wrap.
     * @param <T>    The type of the result.
     * @return A new {@code Async} instance.
     */
    public static <T> Async<T> from(CompletableFuture<T> future) {
        return new Async<>(future);
    }

    /**
     * Creates an {@code Async<Void>} that completes after a given delay.
     *
     * @param duration The duration to wait before completing the task.
     * @return A new {@code Async} instance that completes after the delay.
     */
    public static Async<Void> delay(Duration duration) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> null,
                CompletableFuture.delayedExecutor(duration.toMillis(), TimeUnit.MILLISECONDS)
        );
        return Async.from(future);
    }

    /**
     * Creates an Async that completes when all of the given Asyncs complete.
     * The result is a list of the results of the given Asyncs in the same order.
     * If any of the given Asyncs complete exceptionally, then the returned Async also does so.
     *
     * @param asyncs The list of Async tasks.
     * @param <T>    The type of the results.
     * @return An Async that completes with a list of all results.
     */
    public static <T> Async<List<T>> allOf(List<Async<T>> asyncs) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(
            asyncs.stream().map(Async::toCompletableFuture).toArray(CompletableFuture[]::new)
        );

        CompletableFuture<List<T>> finalFuture = allDoneFuture.thenApply(v ->
            asyncs.stream()
                  .map(Async::await)
                  .collect(Collectors.toList())
        );
        return Async.from(finalFuture);
    }

    /**
     * Creates an Async that completes when any of the given Asyncs complete.
     * The result is the result of the first completed Async.
     * If that first completed Async completed exceptionally, then the returned Async also does so.
     *
     * @param asyncs The list of Async tasks.
     * @return An Async that completes with the result of the first completed task.
     */
    public static Async<Object> anyOf(List<Async<?>> asyncs) {
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(
            asyncs.stream()
                  .map(Async::toCompletableFuture)
                  .toArray(CompletableFuture[]::new)
        );
        return Async.from(anyOfFuture);
    }

    /**
     * Maps the successful result of this Async task to a new value.
     *
     * @param mapper The mapping function.
     * @param <R>    The new result type.
     * @return A new {@code Async} instance with the mapped result.
     */
    public <R> Async<R> map(Function<? super T, ? extends R> mapper) {
        return new Async<>(this.future.thenApply(mapper));
    }

    /**
     * Chains another asynchronous operation after this one completes successfully.
     *
     * @param mapper The function that returns a new {@code Async} task.
     * @param <R>    The new result type.
     * @return A new {@code Async} instance representing the chained operation.
     */
    public <R> Async<R> flatMap(Function<? super T, Async<R>> mapper) {
        return new Async<>(this.future.thenCompose(t -> mapper.apply(t).future));
    }

    /**
     * Recovers from a failure by providing a default value.
     *
     * @param value The default value to use in case of an exception.
     * @return A new {@code Async} instance that will complete with the default value on failure.
     */
    public Async<T> recover(T value) {
        return new Async<>(this.future.exceptionally(ex -> value));
    }

    /**
     * Recovers from a failure by applying a recovery function to the exception.
     *
     * @param recoveryFunction The function to handle the exception and provide a recovery value.
     * @return A new {@code Async} instance with the recovery logic.
     */
    public Async<T> recoverWith(Function<Throwable, T> recoveryFunction) {
        return new Async<>(this.future.exceptionally(recoveryFunction));
    }

    /**
     * Applies a timeout to the asynchronous operation.
     * If the task does not complete within the given duration, the resulting
     * future will be completed exceptionally with a {@link java.util.concurrent.TimeoutException}.
     *
     * @param timeout The maximum time to wait.
     * @param unit    The time unit of the timeout argument.
     * @return A new {@code Async} instance with the timeout applied.
     */
    public Async<T> withTimeout(long timeout, TimeUnit unit) {
        return new Async<>(this.future.orTimeout(timeout, unit));
    }

    /**
     * Blocks and waits for the result, throwing an exception on failure.
     * This is a blocking call and should be used with caution. It will throw a
     * {@link java.util.concurrent.CompletionException} if the underlying future fails.
     *
     * @return The result of the computation.
     */
    public T await() {
        return this.future.join();
    }

    /**
     * Blocks and waits for the result, wrapping it in a {@link Try}.
     * This is a blocking call that provides a safe way to get the result without
     * needing a try-catch block.
     *
     * @return A {@code Try.Success} with the value if completed normally,
     *         or a {@code Try.Failure} with the exception if it failed.
     */
    public Try<T> awaitAsTry() {
        return Try.of(this.future::join);
    }

    /**
     * Returns the underlying CompletableFuture.
     *
     * @return The {@link CompletableFuture}.
     */
    public CompletableFuture<T> toCompletableFuture() {
        return this.future;
    }
}
