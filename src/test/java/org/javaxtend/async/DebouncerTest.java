package org.javaxtend.async;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DebouncerTest {

    private Debouncer<String> debouncer;
    private final AtomicInteger executionCount = new AtomicInteger(0);
    private final AtomicReference<String> lastValue = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        debouncer = new Debouncer<>(value -> {
            executionCount.incrementAndGet();
            lastValue.set(value);
        }, Duration.ofMillis(100));
    }

    @AfterEach
    void tearDown() {
        if (debouncer != null) {
            debouncer.shutdown();
        }
    }

    @Test
    @DisplayName("should execute action only once after a series of rapid calls")
    void executesOnlyOnceAfterRapidCalls() throws InterruptedException {
        debouncer.call("first");
        debouncer.call("second");
        debouncer.call("third");

        Thread.sleep(200);

        assertEquals(1, executionCount.get(), "Action should have been executed only once.");
        assertEquals("third", lastValue.get(), "Only the last value should have been processed.");
    }

    @Test
    @DisplayName("should not execute action if shutdown is called")
    void doesNotExecuteAfterShutdown() throws InterruptedException {
        debouncer.call("test");
        debouncer.shutdown();
        Thread.sleep(200);
        assertEquals(0, executionCount.get());
    }
}