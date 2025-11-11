package org.javaxtend.async;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A utility to limit the execution rate of an action.
 * It ensures that an action is performed at most once per specified time interval.
 * <p>
 * This implementation is thread-safe.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * Throttler throttler = new Throttler(Duration.ofSeconds(1));
 *
 * // This will run
 * throttler.run(() -> System.out.println("Action executed!"));
 *
 * // This will be ignored if called within 1 second of the first call
 * throttler.run(() -> System.out.println("Action ignored."));
 * }</pre></blockquote>
 */
public class Throttler {

    private final long throttlePeriodMillis;
    private final AtomicLong lastExecutionTime = new AtomicLong(0);

    private Throttler(Duration interval) {
        this.throttlePeriodMillis = interval.toMillis();
    }

    public static Throttler of(Duration interval) {
        return new Throttler(interval);
    }

    public boolean run(Runnable action) {
        long now = System.currentTimeMillis();
        long last = lastExecutionTime.get();

        if (now - last > throttlePeriodMillis) {
            if (lastExecutionTime.compareAndSet(last, now)) {
                action.run();
                return true;
            }
        }
        return false;
    }
}
