package org.javaxtend.async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThrottlerTest {

    @Test
    @DisplayName("run() should execute action on first call")
    void run_executesOnFirstCall() {
        Throttler throttler = Throttler.of(Duration.ofSeconds(1));
        AtomicInteger counter = new AtomicInteger(0);

        boolean executed = throttler.run(counter::incrementAndGet);

        assertTrue(executed);
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("run() should not execute action within throttle period")
    void run_doesNotExecuteWithinPeriod() {
        Throttler throttler = Throttler.of(Duration.ofMillis(200));
        AtomicInteger counter = new AtomicInteger(0);

        throttler.run(counter::incrementAndGet);
        boolean executed = throttler.run(counter::incrementAndGet);

        assertFalse(executed);
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("run() should execute action again after throttle period")
    void run_executesAfterPeriod() throws InterruptedException {
        Throttler throttler = Throttler.of(Duration.ofMillis(100));
        AtomicInteger counter = new AtomicInteger(0);

        throttler.run(counter::incrementAndGet);
        Thread.sleep(150);
        boolean executed = throttler.run(counter::incrementAndGet);

        assertTrue(executed);
        assertEquals(2, counter.get());
    }

    @Test
    @DisplayName("run() should be thread-safe")
    void run_isThreadSafe() {
        Throttler throttler = Throttler.of(Duration.ofMillis(50));
        AtomicInteger counter = new AtomicInteger(0);

        assertDoesNotThrow(() -> throttler.run(counter::incrementAndGet));
    }
}