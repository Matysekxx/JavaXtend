package org.javaxtend.functional;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A utility for simulating pattern matching expressions, providing a fluent and
 * type-safe alternative to complex if-else-if chains or switch statements.
 * <p>
 * It allows matching a given value against various conditions (type, predicate, equality)
 * and executing a corresponding function for the first matching case.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * Object value = "hello";
 *
 * String result = Match.of(value)
 *     .when(String.class, s -> s.length() > 10, s -> "A long string")
 *     .when(String.class, s -> "A string of length " + s.length())
 *     .when(Integer.class, i -> "An integer: " + i)
 *     .orElse("Unknown type")
 *     .get();
 *
 * // result will be "A string of length 5"
 * }</pre></blockquote>
 *
 * @param <T> The type of the input value to be matched.
 * @param <R> The type of the result of the match expression.
 */
public class Match<T, R> {
    /** The value to be matched against various conditions. */
    private final T value;
    /** Stores the result of the first successful match, if any. */
    private Maybe<R> result;

    /**
     * Constructs a new {@code Match} instance with the given value.
     *
     * @param value The value to be matched.
     */
    private Match(T value) {
        this.value = value;
        this.result = Maybe.nothing();
    }

    /**
     * Creates a new {@code Match} instance for the given value.
     * This is the entry point for building a match expression.
     *
     * @param value The value to be matched against.
     * @param <T>   The type of the input value.
     * @param <R>   The type of the result.
     * @return A new {@code Match} instance.
     */
    public static <T, R> Match<T, R> of(T value) {
        return new Match<>(value);
    }

    /**
     * Defines a match case based on the type of the input value.
     * If the input value is an instance of {@code type} and no previous {@code when}
     * clause has matched, the {@code mapper} function is applied to the casted value.
     *
     * @param type   The class to match against.
     * @param mapper The function to execute if the type matches.
     * @param <S>    The subtype to match.
     * @return This {@code Match} instance for chaining.
     * @throws NullPointerException if {@code mapper} or {@code type} is null.
     */
    public <S> Match<T, R> when(Class<S> type, Function<S, R> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        Objects.requireNonNull(type, "Type cannot be null");

        if (result.isNothing() && type.isInstance(value))
            this.result = Maybe.just(mapper.apply(type.cast(value)));
        return this;
    }

    /**
     * Defines a match case based on the type of the input value and a predicate.
     * If the input value is an instance of {@code type}, no previous {@code when}
     * clause has matched, AND the {@code predicate} returns true for the casted value,
     * then the {@code mapper} function is applied.
     *
     * @param type      The class to match against.
     * @param predicate The condition (guard) to test the casted value.
     * @param mapper    The function to execute if the type and predicate match.
     * @param <S>       The subtype to match.
     * @return This {@code Match} instance for chaining.
     * @throws NullPointerException if {@code mapper}, {@code predicate}, or {@code type} is null.
     */
    public <S> Match<T, R> when(Class<S> type, Predicate<S> predicate, Function<S, R> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        Objects.requireNonNull(type, "Type cannot be null");

        if (result.isNothing() && type.isInstance(value)){
            S castedValue = type.cast(value);
            if (predicate.test(castedValue))
                this.result = Maybe.just(mapper.apply(castedValue));
        }
        return this;
    }

    /**
     * Defines a match case based on strict equality with another value.
     * If the input value is equal to {@code otherValue} (using {@code Objects.equals()})
     * and no previous {@code when} clause has matched, the {@code supplier} is used
     * to provide the result.
     *
     * @param otherValue The value to compare for equality.
     * @param supplier   The supplier for the result if the values are equal.
     * @return This {@code Match} instance for chaining.
     * @throws NullPointerException if {@code supplier} is null.
     */
    public Match<T, R> when(T otherValue, Supplier<R> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        if (result.isNothing() && Objects.equals(value, otherValue))
            this.result = Maybe.just(supplier.get());
        return this;
    }

    /**
     * Provides a default result if no other {@code when} clause has matched.
     * This is a terminal operation, returning a {@link FinalMatch} instance
     * from which the final result can be retrieved using {@code get()}.
     *
     * @param other The default value to return.
     * @return A {@code FinalMatch} instance with the result.
     */
    public FinalMatch<R> orElse(R other) {
        return new FinalMatch<>(result.orElse(other));
    }

    /**
     * Provides a default result from a supplier if no other {@code when} clause has matched.
     * This is a terminal operation, returning a {@link FinalMatch} instance
     * from which the final result can be retrieved using {@code get()}.
     *
     * @param supplier The supplier for the default value.
     * @return A {@code FinalMatch} instance with the result.
     * @throws NullPointerException if {@code supplier} is null.
     */
    public FinalMatch<R> orElseGet(Supplier<R> supplier) {
        return new FinalMatch<>(result.orElseGet(supplier));
    }

    /**
     * A record representing the final stage of a match expression, holding the computed result.
     *
     * @param finalResult The final computed result of the match expression.
     * @param <R> The type of the result.
     */
    public record FinalMatch<R>(R finalResult) {
        /**
         * Returns the final computed result of the match expression.
         * @return The final result.
         */
        public R get() {
            return finalResult;
        }
    }
}
