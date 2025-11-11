package org.javaxtend.async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    @Test
    @DisplayName("should maintain the specified rate of execution")
    void acquire_maintainsRate() {
        RateLimiter limiter = RateLimiter.create(10.0);
        int permitsToAcquire = 5;
        long expectedTotalTimeMs = (permitsToAcquire - 1) * 100;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < permitsToAcquire; i++) {
            limiter.acquire();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue(duration >= expectedTotalTimeMs,
                "Total duration should be at least " + expectedTotalTimeMs + "ms, but was " + duration + "ms.");

        long margin = 50;
        assertTrue(duration < expectedTotalTimeMs + margin,
                "Total duration should be less than " + (expectedTotalTimeMs + margin) + "ms, but was " + duration + "ms.");
    }
}