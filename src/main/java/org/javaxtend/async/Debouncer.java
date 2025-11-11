package org.javaxtend.async;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A utility class that delays the execution of an action until a specified period
 * of inactivity has passed. If the {@link #call(Object)} method is invoked again
 * before the delay elapses, the timer resets.
 *
 * <p>This is especially useful for scenarios like <em>debounced user input</em>,
 * where an operation (e.g., search query, validation, or API call) should only
 * execute after the user stops typing for a short while.</p>
 *
 * <h2>Core Features</h2>
 * <ul>
 *   <li><b>Automatic reset:</b> Each call restarts the timer, ensuring that only
 *       the most recent value triggers execution.</li>
 *   <li><b>Asynchronous scheduling:</b> The delay is managed by a lightweight
 *       daemon {@link ScheduledExecutorService} thread.</li>
 *   <li><b>Thread-safe:</b> Uses atomic operations to safely cancel and reschedule
 *       pending actions.</li>
 *   <li><b>Clean shutdown:</b> Gracefully terminates the scheduler when no longer needed.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Create a debouncer that executes an action 300ms after typing stops
 * Debouncer<String> searchDebouncer = new Debouncer<>(
 *     query -> System.out.println("Searching for: " + query),
 *     Duration.ofMillis(300)
 * );
 *
 * // Simulate fast user typing — only the last call will trigger the action
 * searchDebouncer.call("j");
 * searchDebouncer.call("ja");
 * searchDebouncer.call("jav");
 * searchDebouncer.call("java"); // Only this triggers after 300ms of silence
 *
 * // Always shut down when done to stop the background thread
 * searchDebouncer.shutdown();
 * }</pre>
 *
 * <h2>Threading Model</h2>
 * <p>
 * Each debouncer instance owns a single daemon thread used to schedule delayed tasks.
 * The action executes on that thread after the specified delay. The {@link #shutdown()}
 * method must be called to terminate the executor when the debouncer is no longer in use.
 * </p>
 *
 * @param <T> the type of the argument passed to the debounced action
 */
public class Debouncer<T> {

    /** The action to execute once the debounce delay elapses. */
    private final Consumer<T> action;

    /** Delay duration in milliseconds. */
    private final long delayMillis;

    /** Single-threaded daemon scheduler for timing the debounce. */
    private final ScheduledExecutorService scheduler;

    /** Holds the currently scheduled (but possibly not yet executed) task. */
    private final AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

    /**
     * Creates a new {@code Debouncer} with the given action and delay.
     *
     * @param action the function to execute after the debounce period
     * @param delay  the delay to wait after the last invocation before executing
     */
    public Debouncer(Consumer<T> action, Duration delay) {
        this.action = action;
        this.delayMillis = delay.toMillis();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Debouncer-Scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Invokes the debouncer with a new value. If another call is made before the
     * delay elapses, the previous scheduled action is cancelled and rescheduled.
     *
     * @param value the latest value to pass to the debounced action
     */
    public void call(T value) {
        ScheduledFuture<?> newFuture = scheduler.schedule(
                () -> action.accept(value),
                delayMillis,
                TimeUnit.MILLISECONDS
        );

        ScheduledFuture<?> oldFuture = futureRef.getAndSet(newFuture);
        if (oldFuture != null) {
            oldFuture.cancel(false);
        }
    }

    /**
     * Shuts down the scheduler immediately and cancels all pending tasks.
     * <p>After this method is called, the debouncer cannot be reused.</p>
     */
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
