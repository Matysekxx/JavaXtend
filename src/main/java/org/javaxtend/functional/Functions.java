package org.javaxtend.functional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A utility class providing higher-order functions and function transformations.
 */
public final class Functions {

    private Functions() {}

    /**
     * Creates a memoized (caching) version of a function.
     * The result of the function is cached for each unique input.
     * Subsequent calls with the same input will return the cached result
     * instead of re-computing it. This implementation is thread-safe.
     *
     * <h3>Example of Usage:</h3>
     * <blockquote><pre>{@code
     * // A slow function
     * Function<String, Integer> slowLength = input -> {
     *     try { Thread.sleep(1000); } catch (InterruptedException e) {}
     *     return input.length();
     * };
     *
     * // Memoize it
     * Function<String, Integer> fastLength = Functions.memoize(slowLength);
     *
     * // First call is slow
     * System.out.println(fastLength.apply("hello")); // Takes 1 second
     *
     * // Second call is instant
     * System.out.println(fastLength.apply("hello")); // Instant
     * }</pre></blockquote>
     *
     * @param function The function to memoize.
     * @param <T> The type of the input to the function.
     * @param <R> The type of the result of the function.
     * @return A new, memoized function.
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        final Map<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }
}