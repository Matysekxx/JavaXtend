package org.javaxtend.functional;

/**
 * A utility class providing common, reusable {@link Validator} implementations.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * Validator<String> passwordValidator = Validators.minLength(8);
 *
 * Result<String, String> result = passwordValidator.validate("short");
 *
 * result.ifError(error -> System.out.println(error)); // Prints: "String must have at least 8 characters."
 * }</pre></blockquote>
 */
public final class Validators {

    private Validators() {}

    /**
     * A validator that checks if an object is not null.
     *
     * @param <T> The type of the object.
     * @return A validator that fails if the input is null.
     */
    public static <T> Validator<T> notNull() {
        return input -> input != null
                ? Result.success(input)
                : Result.error("Input cannot be null.");
    }

    /**
     * A validator that checks if a string is not empty.
     *
     * @return A validator that fails if the string is empty.
     */
    public static Validator<String> notEmpty() {
        return input -> !input.isEmpty()
                ? Result.success(input)
                : Result.error("String cannot be empty.");
    }

    /**
     * A validator that checks if a string has a minimum length.
     *
     * @param minLength The minimum required length.
     * @return A validator for the minimum length of a string.
     */
    public static Validator<String> minLength(int minLength) {
        return input -> input.length() >= minLength
                ? Result.success(input)
                : Result.error("String must have at least " + minLength + " characters.");
    }

    public static Validator<String> maxLength(int maxLength) {
        return input -> input.length() <= maxLength
                ? Result.success(input)
                : Result.error("String must have at most " + maxLength + " characters.");
    }
}