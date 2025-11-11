package org.javaxtend.async;

/**
 * A simple, thread-safe rate limiter based on the token-bucket / fixed-interval concept.
 *
 * <p>This class enforces a steady rate of permits (operations) expressed as
 * "permits per second". Calls to {@link #acquire()} will block the calling thread
 * until a permit is available, thereby throttling the caller to the configured rate.</p>
 *
 * <h2>Behavior</h2>
 * <ul>
 *   <li>The limiter computes a fixed microsecond interval between permits from the configured rate.</li>
 *   <li>Each call to {@link #acquire()} obtains one permit; if a permit is not immediately available,
 *       the call blocks for the required time.</li>
 *   <li>The implementation is intentionally simple and uses a single mutex for synchronization.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create a limiter that allows 2 operations per second (one permit every ~500ms)
 * RateLimiter limiter = RateLimiter.create(2.0);
 *
 * for (int i = 0; i < 5; i++) {
 *     limiter.acquire(); // blocks to maintain the configured rate
 *     System.out.println("Executing task " + i + " at " + System.currentTimeMillis());
 * }
 * }</pre>
 *
 * <h2>Notes</h2>
 * <ul>
 *   <li>This limiter is suitable for coarse-grained throttling (e.g., controlling request rate).</li>
 *   <li>It does not provide burst tokens, warm-up periods, or advanced scheduling found in
 *       more feature-rich implementations (e.g., Guava's {@code RateLimiter}).</li>
 *   <li>{@link #acquire()} swallows {@link InterruptedException}; callers that need interruption
 *       responsiveness should adapt the method accordingly.</li>
 * </ul>
 */
public class RateLimiter {

    /**
     * Interval between permits in microseconds.
     */
    private final long intervalMicros;

    /**
     * The earliest time (in microseconds) at which the next permit becomes available.
     * Access to this field is guarded by {@code mutex}.
     */
    private long nextFreeTicketMicros = 0L;

    /**
     * Mutex protecting updates to {@code nextFreeTicketMicros}.
     */
    private final Object mutex = new Object();

    private RateLimiter(double permitsPerSecond) {
        if (permitsPerSecond <= 0.0 || Double.isNaN(permitsPerSecond)) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        this.intervalMicros = (long) (1_000_000.0 / permitsPerSecond);
    }

    /**
     * Creates a {@code RateLimiter} that issues the given number of permits per second.
     *
     * @param permitsPerSecond the stable throughput expressed as permits per second; must be positive
     * @return a new {@code RateLimiter} configured to the given rate
     * @throws IllegalArgumentException if {@code permitsPerSecond} is not positive
     */
    public static RateLimiter create(double permitsPerSecond) {
        return new RateLimiter(permitsPerSecond);
    }

    /**
     * Acquires a single permit from the rate limiter, blocking the caller until the permit is available.
     *
     * <p>The method calculates how long the caller must wait (in microseconds) until the next permit
     * is allowed, sleeps for that duration if necessary, and then updates the internal ticket time
     * to reflect the consumed permit.</p>
     *
     * <p>Note: {@link InterruptedException} during sleep is caught and ignored in this implementation.
     * If your application requires proper interruption handling, consider wrapping this call and
     * restoring the interrupt status.</p>
     */
    public void acquire() {
        long nowMicros = System.nanoTime() / 1000L;
        synchronized (mutex) {
            long waitMicros = Math.max(0L, nextFreeTicketMicros - nowMicros);
            if (waitMicros > 0L) {
                try {
                    long millis = waitMicros / 1000L;
                    int nanos = (int) ((waitMicros % 1000L) * 1000L);
                    Thread.sleep(millis, nanos);
                } catch (InterruptedException ignored) {
                }
            }
            long current = Math.max(nowMicros, nextFreeTicketMicros);
            nextFreeTicketMicros = current + intervalMicros;
        }
    }
}
