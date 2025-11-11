package org.javaxtend.async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AsyncMemoizerTest {

    @Test
    @DisplayName("should execute the async function only once for the same input")
    void memoize_executesOnlyOnce() {
        final AtomicInteger callCounter = new AtomicInteger(0);

        final Function<String, Async<Integer>> originalFunction = input -> {
            callCounter.incrementAndGet();
            return Async.of(input::length);
        };

        final Function<String, Async<Integer>> memoizedFunction = AsyncMemoizer.memoize(originalFunction);

        Async<Integer> result1 = memoizedFunction.apply("hello");
        assertEquals(5, result1.await());
        assertEquals(1, callCounter.get(), "Function should be called on the first access.");

        Async<Integer> result2 = memoizedFunction.apply("hello");
        assertEquals(5, result2.await());
        assertEquals(1, callCounter.get(), "Function should not be called again for the same input.");
        assertSame(result1, result2, "The same Async instance should be returned for the same input.");

        memoizedFunction.apply("world").await();
        assertEquals(2, callCounter.get(), "Function should be called for a new, different input.");
    }
}