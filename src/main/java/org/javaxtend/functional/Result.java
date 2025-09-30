package org.javaxtend.functional;

public class Result {
    private Result() {
        throw new UnsupportedOperationException("Result is a utility class and cannot be instantiated.");
    }

    /**
     * Represents a successful outcome, encapsulating a value of type {@code T}.
     *
     * @param <T> The type of the success value.
     */
    public static final class Success<T> extends Result {
        private final T value;

        /**
         * Constructs a new {@code Success} instance with the given value.
         *
         * @param value The successful result.
         */
        public Success(T value) {
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
    }

    /**
     * Represents a failed outcome, encapsulating an error of type {@code E}.
     *
     * @param <E> The type of the error.
     */
    public static final class Error<E> extends Result {
        private final E error;

        /**
         * Constructs a new {@code Error} instance with the given error.
         *
         * @param error The error object.
         */
        public Error(E error) {
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
    }

    /**
     * Creates a {@code Success} result with the given value.
     *
     * @param value The value to encapsulate.
     * @param <T> The type of the value.
     * @return A new {@code Success} instance.
     */
    public static <T> Success<T> success(T value) {
        return new Success<>(value);
    }
}
