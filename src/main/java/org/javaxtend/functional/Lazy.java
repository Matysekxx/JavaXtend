package org.javaxtend.functional;

import java.util.function.Supplier;

/**
 * A container for a value that is computed lazily (on first access).
 * This implementation is thread-safe. The value is computed only once and then cached.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * // The expensive calculation will not run until 'get()' is called.
 * Lazy<HeavyObject> lazyObject = Lazy.of(() -> new HeavyObject());
 *
 * // ... some time later
 *
 * // First call to get() triggers the calculation.
 * HeavyObject instance1 = lazyObject.get();
 *
 * // Second call returns the cached instance without re-calculating.
 * HeavyObject instance2 = lazyObject.get();
 *
 * assert instance1 == instance2;
 * }</pre></blockquote>
 *
 * @param <T> The type of the value.
 */
public class Lazy<T> {
    private Supplier<T> supplier;
    private volatile T value;

    /**
     * Returns the lazily-initialized value. If the value has not been initialized yet,
     * this method will trigger the computation from the supplier in a thread-safe manner.
     * Subsequent calls will return the cached value.
     *
     * @return The computed value.
     */
    public T get() {
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = supplier.get();
                    supplier = null;
                }
            }
        }
        return value;
    }

    private Lazy(Supplier<T> supplier) { this.supplier = supplier; }

    /**
     * Creates a new lazy-initialized instance from a supplier.
     *
     * @param supplier The function that provides the value.
     * @param <T> The type of the value.
     * @return A new {@code Lazy} instance.
     */
    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }
}
