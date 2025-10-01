package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void notNull_succeedsForNonNull() {
        Validator<String> validator = Validators.notNull();
        assertTrue(validator.validate("test").isSuccess());
    }

    @Test
    void notNull_failsForNull() {
        Validator<Object> validator = Validators.notNull();
        Result<Object, String> result = validator.validate(null);
        assertTrue(result.isError());
        result.ifError(error -> assertEquals("Input cannot be null.", error));
    }

    @Test
    void notEmpty_succeedsForNonEmptyString() {
        Validator<String> validator = Validators.notEmpty();
        assertTrue(validator.validate("test").isSuccess());
    }

    @Test
    void notEmpty_failsForEmptyString() {
        Validator<String> validator = Validators.notEmpty();
        Result<String, String> result = validator.validate("");
        assertTrue(result.isError());
        result.ifError(error -> assertEquals("String cannot be empty.", error));
    }

    @Test
    void minLength_succeedsWhenLengthIsSufficient() {
        Validator<String> validator = Validators.minLength(5);
        assertTrue(validator.validate("12345").isSuccess());
    }

    @Test
    void minLength_failsWhenLengthIsInsufficient() {
        Validator<String> validator = Validators.minLength(5);
        Result<String, String> result = validator.validate("1234");
        assertTrue(result.isError());
        result.ifError(error -> assertEquals("String must have at least 5 characters.", error));
    }

    @Test
    void maxLength_succeedsWhenLengthIsSufficient() {
        Validator<String> validator = Validators.maxLength(5);
        assertTrue(validator.validate("12345").isSuccess());
    }

    @Test
    void maxLength_failsWhenLengthIsExceeded() {
        Validator<String> validator = Validators.maxLength(5);
        Result<String, String> result = validator.validate("123456");
        assertTrue(result.isError());
        result.ifError(error -> assertEquals("String must have at most 5 characters.", error));
    }

    @Test
    void and_succeedsWhenAllValidatorsPass() {
        Validator<String> validator = Validators.<String>notNull()
                .and(Validators.notEmpty())
                .and(Validators.minLength(3));
        assertTrue(validator.validate("valid").isSuccess());
    }

    @Test
    void and_failsAtFirstFailingValidator() {
        Validator<String> validator = Validators.<String>notNull()
                .and(Validators.minLength(8)) // This one should fail
                .and(Validators.maxLength(5)); // This one should not be reached

        Result<String, String> result = validator.validate("short");
        assertTrue(result.isError());
        result.ifError(error -> assertEquals("String must have at least 8 characters.", error));
    }
}