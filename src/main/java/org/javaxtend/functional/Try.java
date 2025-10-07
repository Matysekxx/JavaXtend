package org.javaxtend.functional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a computation that may either result in a value or throw an exception.
 * It is a functional way to handle exceptions, avoiding explicit try-catch blocks.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * // Wrap a potentially failing operation
 * Try<Integer> result = Try.of(() -> Integer.parseInt("123"));
 *
 * // Process the result safely
 * result.ifSuccess(value -> System.out.println("Parsed: " + value))
 *       .ifFailure(ex -> System.err.println("Failed: " + ex.getMessage()));
 *
 * // Recover from a failure
 * int value = Try.of(() -> Integer.parseInt("abc"))
 *                .recover(ex -> 0) // Provide a default value on failure
 *                .unwrap(); // Safely get the value
 *
 * System.out.println(value); // Prints: 0
 * }</pre></blockquote>
 *
 * @param <T> The type of the success value.
 */
public abstract sealed class Try<T> {

    private Try() {}

    /**
     * Represents a successful computation.
     * @param <T> The type of the success value.
     */
    public static final class Success<T> extends Try<T> {

        private final T value;
        
        private Success(T value) {
            this.value = value;
        }

        /**
         * @return The wrapped value of the successful computation.
         */
        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }
    }
    
    /**
     * Represents a failed computation.
     * @param <T> The type of the value that would have been computed.
     */
    public static final class Failure<T> extends Try<T> {
        private final Throwable cause;

        private Failure(Throwable cause) {
            this.cause = cause;
        }

        /**
         * @return The {@link Throwable} that caused the failure.
         */
        public Throwable getCause() {
            return cause;
        }

        @Override
        public String toString() {
            return "Failure(" + cause.getClass().getSimpleName() + ": " + cause.getMessage() + ")";
        }
    }
    
    /**
     * Creates a {@code Success} instance wrapping the given value.
     * @param value The success value.
     * @param <T> The type of the value.
     * @return A new {@code Success<T>} instance.
     */
    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }
    
    /**
     * Creates a {@code Failure} instance wrapping the given {@link Throwable}.
     * @param cause The exception that caused the failure.
     * @param <T> The type of the value that would have been computed.
     * @return A new {@code Failure<T>} instance.
     */
    public static <T> Try<T> failure(Throwable cause) {
        return new Failure<>(cause);
    }
    
    /**
     * Checks if this is a {@code Success}.
     * @return {@code true} if this is a {@code Success}, {@code false} otherwise.
     */
    public final boolean isSuccess() {
        return this instanceof Success<T>;
    }
    
    /**
     * Checks if this is a {@code Failure}.
     * @return {@code true} if this is a {@code Failure}, {@code false} otherwise.
     */
    public final boolean isFailure() {
        return this instanceof Failure<T>;
    }
    
    /**
     * Performs the given action on the value if this is a {@code Success}.
     * Does nothing if this is a {@code Failure}.
     *
     * @param action The action to perform on the success value.
     * @return This {@code Try} instance, for chaining.
     */
    public final Try<T> ifSuccess(Consumer<? super T> action) {
        if (this instanceof Success<T> s) {
            action.accept(s.getValue());
        }
        return this;
    }
    
    /**
     * Performs the given action on the exception if this is a {@code Failure}.
     * Does nothing if this is a {@code Success}.
     *
     * @param action The action to perform on the failure cause.
     * @return This {@code Try} instance, for chaining.
     */
    public final Try<T> ifFailure(Consumer<? super Throwable> action) {
        if (this instanceof Failure<T> f) {
            action.accept(f.getCause());
        }
        return this;
    }

    /**
     * Transforms this {@code Try} into a single value by applying one of two functions,
     * depending on whether this is a {@code Success} or a {@code Failure}.
     *
     * @param successMapper The function to apply if this is a {@code Success}.
     * @param failureMapper The function to apply if this is a {@code Failure}.
     * @param <R>           The type of the resulting value.
     * @return The transformed value.
     */
    public final <R> R fold(Function<? super T, ? extends R> successMapper, Function<? super Throwable, ? extends R> failureMapper) {
        if (this instanceof Success<T> s) {
            return successMapper.apply(s.getValue());
        } else if (this instanceof Failure<T> f) {
            return failureMapper.apply(f.getCause());
        }
        throw new IllegalStateException("Unreachable code in Try.fold");
    }
    
    /**
     * Returns the value if this is a {@code Success}, otherwise returns the given default value.
     *
     * @param defaultValue The value to return if this is a {@code Failure}.
     * @return The success value or the default value.
     */
    public final T orElse(T defaultValue) {
        if (this instanceof Success<T> s) {
            return s.getValue();
        }
        return defaultValue;
    }
    
    /**
     * Returns the value if this is a {@code Success}, otherwise throws a {@link RuntimeException}
     * wrapping the original cause.
     * <p>
     * This is an unsafe operation and should be used with caution, typically only when a
     * successful result is guaranteed or when a failure should halt the program flow.
     *
     * @return The success value.
     * @throws RuntimeException if this is a {@code Failure}.
     */
    public final T unwrap() {
        if (this instanceof Try.Success<T> s) {
            return s.getValue();
        }
        throw new RuntimeException(((Failure<T>) this).getCause());
    }
    
    /**
     * Wraps a computation that might throw an exception into a {@code Try} instance.
     *
     * @param supplier The {@link Supplier} that provides the value.
     * @param <T> The type of the value.
     * @return A {@code Success} containing the result of the supplier, or a {@code Failure}
     *         containing the exception if one was thrown.
     */
    public static <T> Try<T> of(Supplier<T> supplier) {
        try {
            return success(supplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * If this is a {@code Success}, applies the given mapping function to its value and
     * returns a new {@code Try} wrapping the result. If the mapping function throws an
     * exception, a {@code Failure} is returned.
     * <p>
     * If this is a {@code Failure}, the original {@code Failure} is returned unchanged.
     *
     * @param mapper The mapping function to apply.
     * @param <R> The type of the result of the mapping function.
     * @return A new {@code Try} instance.
     */
    public final <R> Try<R> map(Function<? super T, ? extends R> mapper) {
        if (this instanceof Success<T> s) {
            return Try.of(() -> mapper.apply(s.getValue()));
        }
        return (Try<R>) this;
    }

    /**
     * Similar to {@code map}, but the mapping function returns a {@code Try}.
     * This is useful for chaining operations that can each fail.
     *
     * @param mapper The mapping function to apply.
     * @param <R> The type of the result of the mapping function.
     * @return The result of the mapping function if this is a {@code Success},
     *         otherwise the original {@code Failure}.
     */
    public final <R> Try<R> flatMap(Function<? super T, ? extends Try<R>> mapper) {
        if (this instanceof Success<T> s) {
            return mapper.apply(s.getValue());
        }
        return (Try<R>) this;
    }

    /**
     * If this is a {@code Failure}, applies the given recovery function to the exception
     * to produce a new {@code Try} instance. If this is a {@code Success}, it is returned unchanged.
     *
     * @param recoveryFunction The function to apply to the exception.
     * @return A new {@code Try} instance.
     */
    public final Try<T> recover(Function<? super Throwable, T> recoveryFunction) {
        if (this instanceof Failure<T> f) {
            return Try.of(() -> recoveryFunction.apply(f.getCause()));
        }
        return this;
    }
}
