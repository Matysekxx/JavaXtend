package org.javaxtend.async;

import org.javaxtend.functional.Maybe;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A thread-safe communication channel inspired by Go (Golang) channels.
 *
 * <p>This class provides a simple channel abstraction for passing values between
 * producers and consumers with built-in synchronization and backpressure.
 * It supports both buffered and effectively unbuffered modes:
 * <ul>
 *     <li>Buffered channel — {@code send} blocks only when the buffer is full.</li>
 *     <li>Unbuffered-like behavior — using capacity 0 is represented internally by a queue of size 1,
 *         so {@code send} will block until a consumer takes the value.</li>
 * </ul>
 *
 * <p>Channels can be closed to signal that no more values will be produced. Consumers
 * can iterate over the channel using the {@code for-each} style; iteration ends once
 * the channel is closed and all buffered items have been consumed.</p>
 *
 * <h2>Key Characteristics</h2>
 * <ul>
 *   <li><b>Go-inspired design:</b> The API and behavior draw from Golang channels for simple
 *       synchronous/asynchronous handoff semantics.</li>
 *   <li><b>Thread-safe:</b> Internal queueing and an atomic closed flag ensure safe concurrent use.</li>
 *   <li><b>Blocking semantics:</b> {@code send} and {@code receive} block according to buffer state.</li>
 *   <li><b>Poison pill close:</b> Closing the channel enqueues a terminal marker so blocked consumers are unblocked.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create a channel with a buffer size of 10
 * final Channel<String> urlChannel = new Channel<>(10);
 *
 * // Producer
 * Async.of(() -> {
 *     for (int i = 1; i <= 20; i++) {
 *         urlChannel.send("https://example.com/page/" + i);
 *     }
 *     urlChannel.close();
 *     return null;
 * });
 *
 * // Consumer
 * Async.of(() -> {
 *     for (String url : urlChannel) { // iterates until closed and empty
 *         System.out.println("Processing: " + url);
 *         try { Thread.sleep(100); } catch (InterruptedException ignored) {}
 *     }
 *     System.out.println("All URLs processed.");
 *     return null;
 * });
 * }</pre>
 *
 * <h2>Notes</h2>
 * <ul>
 *   <li>The implementation stores values wrapped in {@link Maybe} to allow a distinct "nothing" marker
 *       used when the channel is closed.</li>
 *   <li>Closing the channel will cause any blocked receivers to become unblocked and receive {@code Maybe.Nothing}.</li>
 *   <li>Senders attempting to {@code send} after the channel is closed will receive {@link IllegalStateException}.</li>
 * </ul>
 *
 * @param <T> the element type carried by this channel
 */
public class Channel<T> implements Iterable<T> {

    private final BlockingQueue<Maybe<T>> queue;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Creates a channel with the specified capacity.
     *
     * @param capacity the maximum number of items the channel can hold (0 treated as unbuffered-like)
     * @throws IllegalArgumentException if capacity is negative
     */
    public Channel(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Channel capacity cannot be negative.");
        }
        // Internally use at least 1 to represent unbuffered-like behavior.
        this.queue = new ArrayBlockingQueue<>(Math.max(1, capacity));
    }

    /**
     * Sends an item into the channel, blocking if the channel buffer is full.
     *
     * @param item the item to send
     * @throws IllegalStateException   if the channel has been closed
     * @throws InterruptedException    if interrupted while waiting to enqueue
     */
    public void send(T item) throws InterruptedException {
        if (closed.get()) {
            throw new IllegalStateException("Cannot send on a closed channel.");
        }
        queue.put(Maybe.just(item));
    }

    /**
     * Receives an item from the channel, blocking if the channel is empty.
     *
     * @return {@code Maybe.Just(value)} for a value, or {@code Maybe.Nothing} if the channel is closed and empty
     * @throws InterruptedException if interrupted while waiting to dequeue
     */
    public Maybe<T> receive() throws InterruptedException {
        return queue.take();
    }

    /**
     * Closes the channel. After closing:
     * <ul>
     *   <li>Further calls to {@code send} will throw {@link IllegalStateException}.</li>
     *   <li>Consumers can continue to drain any buffered items.</li>
     *   <li>Once the buffer is empty, {@code receive} will return {@code Maybe.Nothing}.</li>
     * </ul>
     *
     * The close operation unblocks any waiting receivers by enqueuing a terminal marker.
     */
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                queue.put(Maybe.nothing());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Returns an iterator that consumes values from the channel. The iterator blocks
     * while waiting for items and terminates when the channel is closed and drained.
     *
     * @return an iterator over elements provided by this channel
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Maybe<T> nextItem = null;

            @Override
            public boolean hasNext() {
                if (nextItem == null) {
                    try {
                        nextItem = receive();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        nextItem = Maybe.nothing();
                        return false;
                    }
                }
                return nextItem.isJust();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Channel is closed and empty.");
                }
                T value = nextItem.unwrap();
                nextItem = null;
                return value;
            }
        };
    }
}
