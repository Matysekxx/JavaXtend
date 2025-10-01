package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class LazyTest {

    @Test
    void get_computesValueOnlyOnce() {
        AtomicInteger counter = new AtomicInteger(0);
        Supplier<String> supplier = () -> {
            counter.incrementAndGet();
            return "Computed";
        };

        Lazy<String> lazy = Lazy.of(supplier);
        String value1 = lazy.get();
        assertEquals("Computed", value1);
        assertEquals(1, counter.get());
        String value2 = lazy.get();
        assertEquals("Computed", value2);
        assertEquals(1, counter.get(), "Supplier should not be called again.");
        assertSame(value1, value2);
    }

    @Test
    void get_isThreadSafe() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Supplier<String> supplier = () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter.incrementAndGet();
            return "Thread-Safe-Value";
        };

        Lazy<String> lazy = Lazy.of(supplier);

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                lazy.get();
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        assertEquals(1, counter.get(), "Supplier was called more than once in a multi-threaded environment.");
    }
}