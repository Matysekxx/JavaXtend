package org.javaxtend.functional;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a value of one of two possible types.
 * An instance of {@code Either} is either an instance of {@code Left} or {@code Right}.
 * This is a general-purpose "sum type", in contrast to {@link Result} which has a specific
 * semantic meaning of success or failure.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * // A function that can return either an error code or a success message
 * public Either<Integer, String> processPayment() {
 *     if (paymentSystem.isDown()) {
 *         return Either.left(503); // Service Unavailable
 *     }
 *     return Either.right("Payment successful!");
 * }
 *
 * Either<Integer, String> result = processPayment();
 * String message = result.fold(
 *     errorCode -> "Error: " + errorCode,
 *     successMessage -> "Success: " + successMessage
 * );
 * }</pre></blockquote>
 *
 * @param <L> The type of the 'Left' value.
 * @param <R> The type of the 'Right' value.
 */
public sealed abstract class Either<L, R> {
    private Either() {}

    public static final class Left<L, R> extends Either<L, R> {
        private final L value;
        private Left(L value) {
            this.value = Objects.requireNonNull(value);
        }
        public L getValue() { return value; }
    }

    public static final class Right<L, R> extends Either<L, R> {
        private final R value;
        private Right(R value) {
            this.value = Objects.requireNonNull(value);
        }
        public R getValue() { return value; }
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    public final boolean isLeft() {
        return this instanceof Left;
    }

    public final boolean isRight() {
        return this instanceof Right;
    }

    /**
     * Transforms this {@code Either} into a single value by applying one of two functions,
     * depending on whether this is a {@code Left} or a {@code Right}.
     *
     * @param leftMapper  The function to apply if this is a {@code Left}.
     * @param rightMapper The function to apply if this is a {@code Right}.
     * @param <T>         The type of the resulting value.
     * @return The transformed value.
     */
    public final <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        if (this instanceof Left<L, R> l) {
            return leftMapper.apply(l.getValue());
        } else if (this instanceof Right<L, R> r) {
            return rightMapper.apply(r.getValue());
        }
        throw new IllegalStateException("Unreachable code in Either.fold");
    }
}
