package org.javaxtend.validation;

import java.util.Collection;
import java.util.Objects;

/**
 * Contains the actual guard clause methods for validation.
 */
public class GuardClause {

    /**
     * Throws an {@link IllegalArgumentException} if the input object is null.
     *
     * @param input         The object to validate.
     * @param parameterName The name of the parameter being validated.
     */
    public void isNull(Object input, String parameterName) {
        if (input == null) {
            throw new IllegalArgumentException(parameterName + " cannot be null.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input string is null, empty, or consists of only whitespace.
     *
     * @param input         The string to validate.
     * @param parameterName The name of the parameter being validated, used in the exception message.
     */
    public void nullOrBlank(String input, String parameterName) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(parameterName + " cannot be null or blank.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input string is null or empty.
     *
     * @param input         The string to validate.
     * @param parameterName The name of the parameter being validated.
     */
    public void nullOrEmpty(String input, String parameterName) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException(parameterName + " cannot be null or empty.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input number is negative or zero.
     *
     * @param input         The number to validate.
     * @param parameterName The name of the parameter being validated.
     */
    public void negativeOrZero(long input, String parameterName) {
        if (input <= 0) {
            throw new IllegalArgumentException(parameterName + " must be positive.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input number is negative.
     *
     * @param input         The number to validate.
     * @param parameterName The name of the parameter being validated.
     */
    public void negative(long input, String parameterName) {
        if (input < 0) {
            throw new IllegalArgumentException(parameterName + " cannot be negative.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input number is zero.
     *
     * @param input         The number to validate.
     * @param parameterName The name of the parameter being validated.
     */
    public void zero(long input, String parameterName) {
        if (input == 0) {
            throw new IllegalArgumentException(parameterName + " cannot be zero.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input string does not match the specified regex pattern.
     *
     * @param input         The string to validate.
     * @param pattern       The regex pattern to match against.
     * @param parameterName The name of the parameter being validated.
     */
    public void invalidFormat(String input, String pattern, String parameterName) {
        isNull(input, parameterName);
        if (!input.matches(pattern)) {
            throw new IllegalArgumentException(parameterName + " is not in a valid format.");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input number is outside the specified range.
     * The range is inclusive.
     *
     * @param input         The number to validate.
     * @param min           The minimum allowed value.
     * @param max           The maximum allowed value.
     * @param parameterName The name of the parameter being validated.
     */
    public void outOfRange(int input, int min, int max, String parameterName) {
        if (input < min || input > max) {
            throw new IllegalArgumentException(parameterName + " must be between " + min + " and " + max + ".");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input collection is null or empty.
     *
     * @param input         The collection to validate.
     * @param parameterName The name of the parameter being validated.
     * @param <T>           The type of the elements in the collection.
     */
    public <T> void nullOrEmpty(Collection<T> input, String parameterName) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException(parameterName + " cannot be null or empty.");
        }
    }
}