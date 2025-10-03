package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    void memoize_computesValueOnlyOnceForSameInput() {
        AtomicInteger callCounter = new AtomicInteger(0);

        final Function<String, Integer> originalFunction = input -> {
            callCounter.incrementAndGet();
            return input.length();
        };

        final Function<String, Integer> memoizedFunction = Functions.memoize(originalFunction);

        final int result1 = memoizedFunction.apply("hello");
        assertEquals(5, result1);
        assertEquals(1, callCounter.get(), "Function should be called on the first access.");

        final int result2 = memoizedFunction.apply("hello");
        assertEquals(5, result2);
        assertEquals(1, callCounter.get(), "Function should not be called again for the same input.");
    }

    @Test
    void memoize_computesValueForDifferentInputs() {
        AtomicInteger callCounter = new AtomicInteger(0);
        final Function<String, Integer> originalFunction = input -> {
            callCounter.incrementAndGet();
            return input.length();
        };

        final Function<String, Integer> memoizedFunction = Functions.memoize(originalFunction);

        memoizedFunction.apply("one");
        memoizedFunction.apply("two");
        memoizedFunction.apply("three");

        assertEquals(3, callCounter.get(), "Function should be called for each unique input.");
    }
}