package org.javaxtend.async;

import org.javaxtend.functional.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AsyncRetryTest {

    @Test
    @DisplayName("should succeed on the first attempt")
    void succeedOnFirstAttempt() {
        AtomicInteger counter = new AtomicInteger(0);
        Async<String> result = AsyncRetry.of(() -> {
            counter.incrementAndGet();
            return Async.of(() -> "success");
        }).maxAttempts(3).run();

        assertEquals("success", result.await());
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("should retry and succeed eventually")
    void retryAndSucceed() {
        AtomicInteger counter = new AtomicInteger(0);
        Async<String> result = AsyncRetry.of(() -> {
            if (counter.incrementAndGet() < 3) {
                return Async.from(CompletableFuture.failedFuture(new IOException("fail")));
            }
            return Async.of(() -> "success");
        }).maxAttempts(5).delay(Duration.ofMillis(10)).run();

        assertEquals("success", result.await());
        assertEquals(3, counter.get());
    }

    @Test
    @DisplayName("should fail after all attempts are exhausted")
    void failAfterAllAttempts() {
        AtomicInteger counter = new AtomicInteger(0);
        IOException finalException = new IOException("final failure");

        Async<String> result = AsyncRetry.<String>of(() -> {
            counter.incrementAndGet();
            return Async.from(CompletableFuture.failedFuture(finalException));
        }).maxAttempts(3).delay(Duration.ofMillis(10)).run();

        Try<String> finalResult = result.awaitAsTry();
        assertTrue(finalResult.isFailure());
        assertEquals(3, counter.get());

        Throwable cause = finalResult.fold(v -> null, e -> e);
        assertInstanceOf(CompletionException.class, cause);
        assertEquals(finalException, cause.getCause());
    }

    @Test
    @DisplayName("should not retry if exception type does not match")
    void notRetryOnMismatchedException() {
        AtomicInteger counter = new AtomicInteger(0);
        Async<String> result = AsyncRetry.<String>of(() -> {
            counter.incrementAndGet();
            return Async.from(CompletableFuture.failedFuture(new IllegalArgumentException("config error")));
        })
        .maxAttempts(3)
        .retryOn(IOException.class)
        .run();

        Try<String> finalResult = result.awaitAsTry();
        assertTrue(finalResult.isFailure());
        Throwable cause = finalResult.fold(v -> null, e -> e);
        assertInstanceOf(CompletionException.class, cause);
        assertInstanceOf(IllegalArgumentException.class, cause.getCause());
        assertEquals(1, counter.get(), "Should only be called once");
    }
}