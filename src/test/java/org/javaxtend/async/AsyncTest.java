package org.javaxtend.async;

import org.javaxtend.functional.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsyncTest {

    @Test
    @DisplayName("of() should complete successfully with the correct value")
    void of_completesSuccessfully() {
        Async<String> async = Async.of(() -> "success");
        assertEquals("success", async.await());
    }

    @Test
    @DisplayName("of() with executor should run on the specified executor")
    void of_withExecutor_runsOnExecutor() {
        final String expectedThreadName = "test-executor-thread";
        var executor = Executors.newSingleThreadExecutor(r -> new Thread(r, expectedThreadName));

        Async<String> async = Async.of(() -> Thread.currentThread().getName(), executor);
        String threadName = async.await();

        assertTrue(threadName.contains(expectedThreadName));
        executor.shutdown();
    }

    @Test
    @DisplayName("await() should throw CompletionException on failure")
    void await_throwsOnFailure() {
        Async<Object> async = Async.of(() -> {
            throw new IllegalStateException("failure");
        });
        assertThrows(CompletionException.class, async::await);
    }

    @Test
    @DisplayName("awaitAsTry() should return Success on successful completion")
    void awaitAsTry_returnsSuccess() {
        Async<Integer> async = Async.of(() -> 42);
        Try<Integer> result = async.awaitAsTry();
        assertTrue(result.isSuccess());
        assertEquals(42, result.unwrap());
    }

    @Test
    @DisplayName("awaitAsTry() should return Failure on exceptional completion")
    void awaitAsTry_returnsFailure() {
        var exception = new RuntimeException("test error");
        Async<Object> async = Async.of(() -> { throw exception; });
        Try<Object> result = async.awaitAsTry();

        assertTrue(result.isFailure());
        assertInstanceOf(CompletionException.class, ((Try.Failure<Object>) result).getCause());
        assertEquals(exception, ((Try.Failure<Object>) result).getCause().getCause());
    }

    @Test
    @DisplayName("map() should transform the successful result")
    void map_transformsResult() {
        Async<Integer> async = Async.of(() -> 5).map(x -> x * 2);
        assertEquals(10, async.await());
    }

    @Test
    @DisplayName("recover() should provide a default value on failure")
    void recover_providesDefaultValue() {
        Async<String> async = Async.<String>of(() -> { throw new RuntimeException("error"); }).recover("recovered");
        assertEquals("recovered", async.await());
    }

    @Test
    @DisplayName("withTimeout() should throw TimeoutException if task is too slow")
    void withTimeout_throwsException() {
        Async<String> slowTask = Async.of(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "done";
        }).withTimeout(50, TimeUnit.MILLISECONDS);

        Try<String> result = slowTask.awaitAsTry();
        assertTrue(result.isFailure());
        assertInstanceOf(TimeoutException.class, result.fold(v -> null, Throwable::getCause));
    }

    @Test
    @DisplayName("allOf() should complete when all tasks are done")
    void allOf_completesWhenAllDone() {
        Async<String> task1 = Async.of(() -> "one");
        Async<String> task2 = Async.of(() -> "two");

        Async<List<String>> all = Async.allOf(List.of(task1, task2));
        List<String> results = all.await();

        assertEquals(List.of("one", "two"), results);
    }

    @Test
    @DisplayName("allOf() should fail if one task fails")
    void allOf_failsIfOneFails() {
        Async<Object> task1 = Async.of(() -> "one");
        Async<Object> task2 = Async.of(() -> { throw new RuntimeException("failure"); });

        Async<List<Object>> all = Async.allOf(List.of(task1, task2));

        assertThrows(CompletionException.class, all::await);
    }

    @Test
    @DisplayName("anyOf() should complete with the first finished task")
    void anyOf_completesWithFirst() {
        Async<String> slowTask = Async.delay(Duration.ofMillis(200)).flatMap(v -> Async.of(() -> "slow"));
        Async<String> fastTask = Async.of(() -> "fast");

        Async<Object> any = Async.anyOf(List.of(slowTask, fastTask));
        Object result = any.await();

        assertEquals("fast", result);
    }

    @Test
    @DisplayName("delay() should complete after the specified duration")
    void delay_completesAfterDuration() {
        long startTime = System.currentTimeMillis();
        Async.delay(Duration.ofMillis(100)).await();
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime >= 100, "Delay should be at least 100ms");
    }
}