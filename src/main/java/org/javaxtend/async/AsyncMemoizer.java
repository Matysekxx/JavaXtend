package org.javaxtend.async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A utility class that provides memoization (caching) for asynchronous computations.
 *
 * <p>This class allows you to wrap a function that produces an {@link Async} task,
 * ensuring that the result for each unique input is computed only once. Subsequent
 * calls with the same argument return the cached {@code Async} instance immediately,
 * avoiding redundant asynchronous executions.</p>
 *
 * <h2>Core Features</h2>
 * <ul>
 *   <li><b>Automatic caching:</b> Each unique input is evaluated once, and its result is reused.</li>
 *   <li><b>Thread-safe:</b> Uses a {@link ConcurrentHashMap} to safely handle concurrent access.</li>
 *   <li><b>Seamless integration:</b> Works with any {@link Async} operation — network calls, database queries, etc.</li>
 *   <li><b>Lazy evaluation:</b> The original function is executed only when its result is first requested.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // An expensive async function
 * Function<Integer, Async<String>> fetchProduct = productId ->
 *     Async.of(() -> {
 *         System.out.println("Fetching product " + productId + " from database...");
 *         Thread.sleep(1000);
 *         return "Product " + productId;
 *     });
 *
 * // Create a memoized version
 * Function<Integer, Async<String>> fastFetch = AsyncMemoizer.memoize(fetchProduct);
 *
 * // First call — executes the async operation
 * fastFetch.apply(123).await(); // Prints: Fetching product 123 from database...
 *
 * // Second call — instant, uses the cached Async result
 * fastFetch.apply(123).await(); // No print, uses cache
 * }</pre>
 *
 * <h2>Threading Model</h2>
 * <p>All caching is handled in-memory via a thread-safe {@link ConcurrentHashMap}.
 * The {@link Async} computations themselves are executed using the configuration of
 * the original {@code Async} instances; the memoizer does not modify threading behavior.</p>
 *
 * <h2>Usage Notes</h2>
 * <ul>
 *   <li>The cache stores {@link Async} instances, not their unwrapped values. If you want
 *       to cache final results, ensure your {@code Async} resolves deterministically.</li>
 *   <li>The cache lives as long as the returned function reference — there is no expiration
 *       or invalidation mechanism built in.</li>
 * </ul>
 *
 * @see Async
 */
public final class AsyncMemoizer {

    /** Private constructor to prevent instantiation. */
    private AsyncMemoizer() {}

    /**
     * Returns a memoized version of the given asynchronous function.
     * <p>Each distinct input value is cached along with its {@link Async} result.
     * Subsequent invocations with the same input will return the cached {@code Async}.</p>
     *
     * @param function the asynchronous function to memoize
     * @param <T> the input type
     * @param <R> the result type
     * @return a memoized version of the provided function
     */
    public static <T, R> Function<T, Async<R>> memoize(Function<T, Async<R>> function) {
        final Map<T, Async<R>> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }
}
