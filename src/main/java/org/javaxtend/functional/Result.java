package org.javaxtend.functional;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A sealed, generic class representing the result of an operation that can either succeed (`Success`)
 * or fail (`Error`).
 * It is a type-safe alternative to returning null or throwing exceptions for expected failures.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 * public Result<Double, String> divide(double a, double b) {
 *     if (b == 0) {
 *         return Result.error("Cannot divide by zero.");
 *     }
 *     return Result.success(a / b);
 * }
 *
 * Result<Double, String> result = divide(10.0, 0.0);
 * result
 *     .ifSuccess(value -> System.out.println("Result is: " + value))
 *     .ifError(error -> System.err.println("Error occurred: " + error));
 * </pre></blockquote>
 */
public sealed abstract class Result<T, E> {
    private Result() {
    }

    /**
     * Represents a successful outcome, encapsulating a value of type {@code T}.
     *
     * @param <T> The type of the success value (unused in Error).
     * @param <E> The type of the error.
     */
    public static final class Success<T, E> extends Result<T, E> {
        private final T value;

        /**
         * Constructs a new {@code Success} instance with the given value.
         *
         * @param value The successful result.
         */
        private Success(T value) {
            this.value = value;
        }

        /**
         * Returns the encapsulated success value.
         *
         * @return The success value.
         */
        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }

        @Override
        public <R> Result<R, E> flatMap(Function<? super T, ? extends Result<R, E>> mapper) {
            return Objects.requireNonNull(mapper.apply(getValue()));
        }
    }

    /**
     * Represents a failed outcome, encapsulating an error of type {@code E}.
     *
     * @param <T> The type of the success value (unused in Error).
     * @param <E> The type of the error value.
     */
    public static final class Error<T, E> extends Result<T, E> {
        private final E error;

        /**
         * Constructs a new {@code Error} instance with the given error.
         *
         * @param error The error object.
         */
        private Error(E error) {
            this.error = error;
        }

        /**
         * Returns the encapsulated error.
         *
         * @return The error object.
         */
        public E getError() {
            return error;
        }

        @Override
        public String toString() {
            return "Error(" + error + ")";
        }

        @Override
        public <R> Result<R, E> flatMap(Function<? super T, ? extends Result<R, E>> mapper) {
            return Result.error(this.error);
        }
    }

    /**
     * Creates a {@code Success} result with the given value.
     *
     * @param value The value to encapsulate.
     * @param <T> The type of the success value.
     * @param <E> The type of the error value.
     * @return A new {@code Success} instance.
     */
    public static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates an {@code Error} result with the given error object.
     *
     * @param error The error object to encapsulate.
     * @param <T> The type of the success value.
     * @param <E> The type of the error value.
     * @return A new {@code Error} instance.
     */
    public static <T, E> Result<T, E> error(E error) {
        return new Error<>(error);
    }

    /**
     * Executes the given action if this result is a {@code Success}.
     *
     * @param action The action to execute, which consumes the success value.
     * @return The current {@code Result} instance, allowing for method chaining.
     */
    public final Result<T, E> ifSuccess(Consumer<? super T> action) {
        if (this instanceof Success<T, E> s) {
            action.accept(s.getValue());
        }
        return this;
    }

    /**
     * Executes the given action if this result is an {@code Error}.
     *
     * @param action The action to execute, which consumes the error value.
     * @return The current {@code Result} instance, allowing for method chaining.
     */
    public final Result<T, E> ifError(Consumer<? super E> action) {
        if (this instanceof Error<T, E> e) {
            action.accept(e.getError());
        }
        return this;
    }

    /**
     * Transforms this {@code Result} into a single value by applying one of two functions,
     * depending on whether this is a {@code Success} or an {@code Error}.
     *
     * @param successMapper The function to apply if this is a {@code Success}.
     * @param errorMapper The function to apply if this is an {@code Error}.
     * @param <R>         The type of the resulting value.
     * @return The transformed value.
     */
    public final <R> R fold(Function<? super T, ? extends R> successMapper, Function<? super E, ? extends R> errorMapper) {
        if (this instanceof Success<T, E> s) {
            return successMapper.apply(s.getValue());
        } else if (this instanceof Error<T, E> e) {
            return errorMapper.apply(e.getError());
        }
        throw new IllegalStateException("Unreachable code in Result.fold"); // Should not happen
    }


    /**
     * Returns the success value or the provided default value if the result is an {@code Error}.
     *
     * @param defaultValue The value to return if this is an {@code Error}.
     * @return The success value or the default value.
     */
    public T orElse(T defaultValue) {
        if (this instanceof Result.Success<T,E> s) {
            return s.getValue();
        }
        return defaultValue;
    }


    /**
     * Returns the success value or computes it from the given supplier if the result is an {@code Error}.
     *
     * @param supplier A {@code Supplier} whose result is returned if this is an {@code Error}.
     * @return The success value or the value from the supplier.
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        if (this instanceof Result.Success<T,E> s) return s.getValue();
        return supplier.get();
    }


    /**
     * Returns the contained {@code Success} value.
     * Throws a {@code RuntimeException} if the result is an {@code Error}.
     *
     * @return The encapsulated value if this result is a {@code Success}.
     * @throws RuntimeException if this result is an {@code Error}.
     */
    public T unwrap() {
        if (this instanceof Result.Success<T,E> s) {
            return s.getValue();
        }
        throw new RuntimeException(
                "Called unwrap() on an Error value: "
                        + ((Error<T, E>) this).getError()
        );
    }

    /**
     * Returns {@code true} if this result is a {@code Success}.
     *
     * @return {@code true} if this is a {@code Success}, {@code false} otherwise.
     */
    public final boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * Returns {@code true} if this result is an {@code Error}.
     *
     * @return {@code true} if this is an {@code Error}, {@code false} otherwise.
     */
    public final boolean isError() {
        return this instanceof Error;
    }

    /**
     * Returns the contained {@code Success} value or throws an exception created by the provided supplier.
     *
     * @param exceptionSupplier The supplier which will return the exception to be thrown.
     * @param <X> The type of the exception to be thrown.
     * @return The encapsulated value if this result is a {@code Success}.
     * @throws X if this result is an {@code Error}.
     */
    public <X extends Throwable> T unwrapOrThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this instanceof Success<T, E> s) {
            return s.getValue();
        }
        throw exceptionSupplier.get();
    }

    /**
     * If the result is a {@code Success}, applies the provided {@code Result}-bearing
     * mapping function to its value, otherwise returns the original {@code Error}.
     * This method is useful for chaining operations that may each fail.
     *
     * @param mapper A function to apply to the success value.
     * @param <R> The success type of the value returned by the mapping function.
     * @return A new {@code Result} with the transformed value.
     */
    public abstract <R> Result<R, E> flatMap(Function<? super T, ? extends Result<R, E>> mapper);

}
