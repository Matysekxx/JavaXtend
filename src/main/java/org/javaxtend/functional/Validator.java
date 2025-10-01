package org.javaxtend.functional;

/**
 * A functional interface for defining a validation rule.
 * It takes an input of type {@code T} and returns a {@link Result}
 * indicating success or failure with an error message.
 *
 * @param <T> The type of the object to validate.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>{@code
 * // Create a composite validator for a username
 * Validator<String> usernameValidator = Validators.<String>notNull()
 *         .and(Validators.notEmpty())
 *         .and(Validators.minLength(5));
 *
 * // Use the validator
 * Result<String, String> result = usernameValidator.validate("JohnDoe");
 * result.ifSuccess(username -> System.out.println("Username is valid!"));
 * }</pre></blockquote>
 */
public interface Validator<T> {

    /**
     * Validates the given input.
     *
     * @param input The object to validate.
     * @return A {@code Result.Success} containing the input if validation passes,
     *         or a {@code Result.Error} with an error message if it fails.
     */
    Result<T, String> validate(T input);

    /**
     * Combines this validator with another one. If this validator succeeds,
     * the other validator is then applied.
     *
     * @param other The next validator to apply.
     * @return A new validator that represents the composition of this and the other validator.
     */
    default Validator<T> and(Validator<T> other) {
        return input -> this.validate(input).flatMap(ignored -> other.validate(input));
    }
}