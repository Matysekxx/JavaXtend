package org.javaxtend.util;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * A utility that simulates Go's `defer` statement using Java's `try-with-resources`.
 * <p>
 * A deferred function's execution is postponed until the surrounding scope exits.
 * Deferred calls are executed in Last-In, First-Out (LIFO) order.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * try (var d = Defer.create()) {
 *     System.out.println("Main logic start");
 *     d.defer(() -> System.out.println("Executed last (first deferred)"));
 *     d.defer(() -> System.out.println("Executed first (last deferred)"));
 *     System.out.println("Main logic end");
 * } // Deferred actions are executed here
 * }</pre></blockquote>
 */
public final class Defer implements AutoCloseable {
    private final Deque<Runnable> actions = new ArrayDeque<>();
    private Defer() {}

    public static Defer create() {
        return new Defer();
    }

    /**
     * Registers an action to be executed when the scope is exited.
     * @param action The action to defer.
     */
    public void defer(Runnable action) {
        actions.push(action);
    }


    /**
     * Executes all deferred actions in LIFO order.
     * This method is automatically called by the `try-with-resources` statement.
     */
    @Override
    public void close() {
        while (!actions.isEmpty()) {
            actions.pop().run();
        }
    }
}
