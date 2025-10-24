package org.javaxtend.functional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents an optional value: every {@code Maybe} is either {@code Just} and contains a value,
 * or {@code Nothing}, and does not. It is a robust, type-safe alternative to using {@code null}.
 *
 * <h2>Example of Usage:</h2>
 * <p>
 * Instead of returning {@code null} and forcing null-checks, a method can return a {@code Maybe}.
 * This makes the possibility of an absent value explicit in the method's signature.
 * </p>
 * <blockquote><pre>{@code
 * // A function that might not find a user
 * public Maybe<User> findUserById(int id) {
 *     // ... database logic that returns a nullable User
 *     return Maybe.ofNullable(userFromDb);
 * }
 *
 * // Chaining operations to safely access nested data
 * String profileUrl = findUserById(1)
 *         .flatMap(User::getProfile) // getProfile() returns Maybe<Profile>
 *         .map(Profile::getUrl)      // getUrl() returns String
 *         .orElse("/images/default-avatar.png");
 *
 * System.out.println(profileUrl);
 * }</pre></blockquote>
 *
 * @param <T> The type of the non-null value.
 */
public sealed abstract class Maybe<T> {
    private Maybe() {}

    /**
     * Represents a non-empty {@code Maybe} that contains a value.
     */
    public static final class Just<T> extends Maybe<T> {
        private final T value;

        private Just(T value) {
            this.value = Objects.requireNonNull(value, "Just value cannot be null");
        }

        public T getValue() {
            return value;
        }

        @Override
        public <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
            return Maybe.just(mapper.apply(value));
        }

        @Override
        public <R> Maybe<R> flatMap(Function<? super T, ? extends Maybe<R>> mapper) {
            return Objects.requireNonNull(mapper.apply(value));
        }

        @Override
        public Maybe<T> filter(Predicate<? super T> predicate) {
            return predicate.test(value) ? this : Maybe.nothing();
        }
    }

    /**
     * Represents an empty {@code Maybe} with no value.
     */
    public static final class Nothing<T> extends Maybe<T> {
        private Nothing() {}

        @Override
        public <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
            return Maybe.nothing();
        }

        @Override
        public <R> Maybe<R> flatMap(Function<? super T, ? extends Maybe<R>> mapper) {
            return Maybe.nothing();
        }

        @Override
        public Maybe<T> filter(Predicate<? super T> predicate) {
            return Maybe.nothing();
        }
    }
    
    

    /**
     * Creates a {@code Maybe} containing the given non-null value.
     *
     * @param value The value to wrap, must not be null.
     * @return A {@code Maybe.Just} containing the value.
     * @throws NullPointerException if value is null.
     */
    public static <T> Maybe<T> just(T value) {
        return new Just<>(value);
    }

    /**
     * Returns an empty {@code Maybe} instance.
     *
     * @return A {@code Maybe.Nothing}.
     */
    public static <T> Maybe<T> nothing() {
        return new Nothing<>();
    }

    /**
     * Creates a {@code Maybe} from a value that might be null.
     *
     * @param value The value, which can be null.
     * @return A {@code Maybe.Just} if the value is not null, otherwise {@code Maybe.Nothing}.
     */
    public static <T> Maybe<T> ofNullable(T value) {
        return value == null ? nothing() : just(value);
    }

    /**
     * Returns {@code true} if this is a {@code Just}, {@code false} otherwise.
     */
    public final boolean isJust() {
        return this instanceof Just;
    }

    /**
     * Returns {@code true} if this is a {@code Nothing}, {@code false} otherwise.
     */
    public final boolean isNothing() {
        return this instanceof Nothing;
    }

    /**
     * If a value is present, performs the given action with the value, otherwise does nothing.
     *
     * @param action The action to be performed, if a value is present.
     * @return The current {@code Maybe} instance for chaining.
     */
    public final Maybe<T> ifJust(Consumer<? super T> action) {
        if (this instanceof Just<T> s) {
            action.accept(s.getValue());
        }
        return this;
    }

    /**
     * Returns the value if present, otherwise returns {@code other}.
     *
     * @param other The value to be returned if there is no value present.
     * @return The value, if present, otherwise {@code other}.
     */
    public T orElse(T other) {
        if (this instanceof Just<T> s) {
            return s.getValue();
        }
        return other;
    }

    /**
     * Returns the value if present, otherwise returns the result produced by the supplying function.
     *
     * @param supplier The supplying function that produces a value to be returned.
     * @return The value, if present, otherwise the result of the supplying function.
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        if (this instanceof Just<T> s) {
            return s.getValue();
        }
        return supplier.get();
    }

    /**
     * Returns the value if present, otherwise throws an exception.
     *
     * @return The non-null value described by this {@code Maybe}.
     * @throws java.util.NoSuchElementException if there is no value present.
     */
    public T unwrap() {
        if (this instanceof Just<T> s) {
            return s.getValue();
        }
        throw new NoSuchElementException("Called unwrap() on a Nothing value");
    }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and return a {@code Maybe} describing the result. Otherwise, return an
     * empty {@code Maybe}.
     *
     * @param <R> The type of the result of the mapping function
     * @param mapper A mapping function to apply to the value, if present
     * @return A {@code Maybe} describing the result of applying a mapping
     *         function to the value of this {@code Maybe}, if a value is present,
     *         otherwise an empty {@code Maybe}.
     */
    public abstract <R> Maybe<R> map(Function<? super T, ? extends R> mapper);

    /**
     * If a value is present, apply the provided {@code Maybe}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code Maybe}. This method is similar to {@link #map(Function)}, but the
     * mapping function is one whose result is already a {@code Maybe}.
     *
     * @param <R> The type of the result of the mapping function
     * @param mapper A mapping function to apply to the value, if present
     * @return The result of applying a {@code Maybe}-bearing mapping
     *         function to the value of this {@code Maybe}, if a value is present,
     *         otherwise an empty {@code Maybe}.
     */
    public abstract <R> Maybe<R> flatMap(Function<? super T, ? extends Maybe<R>> mapper);

    /**
     * If a value is present, and the value matches the given predicate,
     * return a {@code Maybe} describing the value, otherwise return an
     * empty {@code Maybe}.
     *
     * @param predicate A predicate to apply to the value, if present.
     * @return A {@code Maybe} describing the value of this {@code Maybe},
     *         if a value is present and the value matches the given predicate,
     *         otherwise an empty {@code Maybe}.
     */
    public abstract Maybe<T> filter(Predicate<? super T> predicate);
}