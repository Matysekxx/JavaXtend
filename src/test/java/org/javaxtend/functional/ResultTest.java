package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void success_createsSuccessInstance() {
        Result<Integer, String> result = Result.success(42);
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
    }

    @Test
    void error_createsErrorInstance() {
        Result<Integer, String> result = Result.error("Something went wrong");
        assertTrue(result.isError());
        assertFalse(result.isSuccess());
    }

    @Test
    void ifSuccess_executesActionOnSuccess() {
        final boolean[] executed = {false};
        Result<String, Integer> result = Result.success("Hello");
        result.ifSuccess(value -> {
            assertEquals("Hello", value);
            executed[0] = true;
        });
        assertTrue(executed[0]);
    }

    @Test
    void ifSuccess_doesNotExecuteActionOnError() {
        Result<String, Integer> result = Result.error(123);
        result.ifSuccess(value -> fail("Should not be executed on Error"));
    }

    @Test
    void ifError_executesActionOnError() {
        final boolean[] executed = {false};
        Result<String, Integer> result = Result.error(404);
        result.ifError(error -> {
            assertEquals(404, error);
            executed[0] = true;
        });
        assertTrue(executed[0]);
    }

    @Test
    void orElse_returnsValueOnSuccess() {
        Result<Integer, String> result = Result.success(100);
        assertEquals(100, result.orElse(0));
    }

    @Test
    void orElse_returnsDefaultOnError() {
        Result<Integer, String> result = Result.error("File not found");
        assertEquals(0, result.orElse(0));
    }

    @Test
    void unwrap_returnsValueOnSuccess() {
        Result<String, Integer> result = Result.success("Data");
        assertEquals("Data", result.unwrap());
    }

    @Test
    void unwrap_throwsExceptionOnError() {
        Result<String, String> result = Result.error("Critical failure");
        RuntimeException exception = assertThrows(RuntimeException.class, result::unwrap);
        assertTrue(exception.getMessage().contains("Critical failure"));
    }

    @Test
    void unwrapOrThrow_returnsValueOnSuccess() throws IOException {
        Result<Integer, String> result = Result.success(200);
        assertEquals(200, result.unwrapOrThrow(() -> new IOException("Should not happen")));
    }

    @Test
    void unwrapOrThrow_throwsCustomExceptionOnError() {
        Result<Integer, String> result = Result.error("Access denied");
        assertThrows(IOException.class, () -> result.unwrapOrThrow(() -> new IOException("Custom error message")));
    }

    @Test
    void foldOnSuccessShouldApplySuccessMapper() {
        Result<String, Integer> successResult = Result.success("OK");

        String result = successResult.fold(
                s -> "Success: " + s,
                e -> "Error: " + e
        );

        assertEquals("Success: OK", result);
    }

    @Test
    void foldOnErrorShouldApplyErrorMapper() {
        Result<String, Integer> errorResult = Result.error(404);
        String result = errorResult.fold(s -> "Success: " + s, e -> "Error: " + e);
        assertEquals("Error: 404", result);
    }

    @Test
    void ok_onSuccess_returnsJust() {
        Result<String, Integer> success = Result.success("data");
        Maybe<String> maybe = success.ok();
        assertTrue(maybe.isJust());
        assertEquals("data", maybe.unwrap());
    }

    @Test
    void ok_onError_returnsNothing() {
        Result<String, Integer> error = Result.error(404);
        Maybe<String> maybe = error.ok();
        assertTrue(maybe.isNothing());
    }

    @Test
    void err_onSuccess_returnsNothing() {
        Result<String, Integer> success = Result.success("data");
        Maybe<Integer> maybe = success.err();
        assertTrue(maybe.isNothing());
    }

    @Test
    void err_onError_returnsJust() {
        Result<String, Integer> error = Result.error(404);
        Maybe<Integer> maybe = error.err();
        assertTrue(maybe.isJust());
        assertEquals(404, maybe.unwrap());
    }

    @Test
    void transpose_convertsCorrectly() {
        Result<Maybe<String>, Integer> successJust = Result.success(Maybe.just("value"));
        assertEquals(Maybe.just(Result.success("value")), successJust.transpose());

        Result<Maybe<String>, Integer> successNothing = Result.success(Maybe.nothing());
        assertEquals(Maybe.nothing(), successNothing.transpose());

        Result<Maybe<String>, Integer> errorResult = Result.error(404);
        assertEquals(Maybe.just(Result.error(404)), errorResult.transpose());
    }
}