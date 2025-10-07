package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    @Test
    void of_shouldReturnSuccess_whenSupplierSucceeds() {
        Try<String> result = Try.of(() -> "Success");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals("Success", result.unwrap());
    }

    @Test
    void of_shouldReturnFailure_whenSupplierThrowsException() {
        RuntimeException exception = new RuntimeException("Error");
        Try<String> result = Try.of(() -> {
            throw exception;
        });

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());
        assertEquals(exception, ((Try.Failure<String>) result).getCause());
    }

    @Test
    void success_shouldCreateSuccessInstance() {
        Try<Integer> success = Try.success(123);

        assertTrue(success.isSuccess());
        assertEquals(123, ((Try.Success<Integer>) success).getValue());
    }

    @Test
    void failure_shouldCreateFailureInstance() {
        IllegalStateException cause = new IllegalStateException("State error");
        Try<Integer> failure = Try.failure(cause);

        assertTrue(failure.isFailure());
        assertEquals(cause, ((Try.Failure<Integer>) failure).getCause());
    }

    @Test
    void ifSuccess_shouldExecuteAction_forSuccess() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Try<String> success = Try.success("test");

        success.ifSuccess(value -> {
            assertEquals("test", value);
            executed.set(true);
        });

        assertTrue(executed.get());
    }

    @Test
    void ifSuccess_shouldNotExecuteAction_forFailure() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Try<String> failure = Try.failure(new Exception());

        failure.ifSuccess(value -> executed.set(true));

        assertFalse(executed.get());
    }

    @Test
    void ifFailure_shouldExecuteAction_forFailure() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Exception cause = new Exception("reason");
        Try<String> failure = Try.failure(cause);

        failure.ifFailure(ex -> {
            assertEquals(cause, ex);
            executed.set(true);
        });

        assertTrue(executed.get());
    }

    @Test
    void ifFailure_shouldNotExecuteAction_forSuccess() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Try<String> success = Try.success("test");

        success.ifFailure(ex -> executed.set(true));

        assertFalse(executed.get());
    }

    @Test
    void orElse_shouldReturnValue_forSuccess() {
        Try<Integer> success = Try.success(42);
        assertEquals(42, success.orElse(0));
    }

    @Test
    void orElse_shouldReturnDefaultValue_forFailure() {
        Try<Integer> failure = Try.failure(new Exception());
        assertEquals(0, failure.orElse(0));
    }

    @Test
    void unwrap_shouldReturnValue_forSuccess() {
        Try<String> success = Try.success("unwrapped");
        assertEquals("unwrapped", success.unwrap());
    }

    @Test
    void unwrap_shouldThrowRuntimeException_forFailure() {
        Exception cause = new Exception("Original cause");
        Try<String> failure = Try.failure(cause);

        RuntimeException thrown = assertThrows(RuntimeException.class, failure::unwrap);
        assertEquals(cause, thrown.getCause());
    }

    @Test
    void map_shouldTransformValue_forSuccess() {
        Try<Integer> initial = Try.success(5);
        Try<String> mapped = initial.map(String::valueOf);

        assertTrue(mapped.isSuccess());
        assertEquals("5", mapped.unwrap());
    }

    @Test
    void map_shouldReturnFailure_whenMapperThrowsException() {
        Try<Integer> initial = Try.success(5);
        Try<String> mapped = initial.map(val -> {
            throw new IllegalStateException("Mapping failed");
        });

        assertTrue(mapped.isFailure());
        assertTrue(((Try.Failure<String>) mapped).getCause() instanceof IllegalStateException);
    }

    @Test
    void map_shouldReturnSameFailure_forFailure() {
        Try<Integer> initial = Try.failure(new Exception("Initial error"));
        Try<String> mapped = initial.map(String::valueOf);

        assertSame(initial, mapped);
    }

    @Test
    void flatMap_shouldChainToSuccess_forSuccess() {
        Try<Integer> initial = Try.success(10);
        Try<String> flatMapped = initial.flatMap(val -> Try.success("Value is " + val));

        assertTrue(flatMapped.isSuccess());
        assertEquals("Value is 10", flatMapped.unwrap());
    }

    @Test
    void flatMap_shouldChainToFailure_forSuccess() {
        Try<Integer> initial = Try.success(10);
        Exception flatMapException = new Exception("FlatMap error");
        Try<String> flatMapped = initial.flatMap(val -> Try.failure(flatMapException));

        assertTrue(flatMapped.isFailure());
        assertEquals(flatMapException, ((Try.Failure<String>) flatMapped).getCause());
    }

    @Test
    void flatMap_shouldReturnSameFailure_forFailure() {
        Try<Integer> initial = Try.failure(new Exception("Initial error"));
        Try<String> flatMapped = initial.flatMap(val -> Try.success("wont happen"));

        assertSame(initial, flatMapped);
    }

    @Test
    void recover_shouldNotBeApplied_forSuccess() {
        Try<String> success = Try.success("I am fine");
        Try<String> recovered = success.recover(Throwable::getMessage);

        assertSame(success, recovered);
        assertEquals("I am fine", recovered.unwrap());
    }

    @Test
    void recover_shouldTransformFailureToSuccess() {
        Try<String> failure = Try.failure(new Exception("error"));
        Try<String> recovered = failure.recover(ex -> "recovered from " + ex.getMessage());

        assertTrue(recovered.isSuccess());
        assertEquals("recovered from error", recovered.unwrap());
    }

    @Test
    void recover_shouldReturnNewFailure_whenRecoveryFunctionThrows() {
        Try<String> failure = Try.failure(new Exception("original error"));
        Try<String> recovered = failure.recover(ex -> {
            throw new IllegalStateException("recovery failed");
        });

        assertTrue(recovered.isFailure());
        assertTrue(((Try.Failure<String>) recovered).getCause() instanceof IllegalStateException);
        assertEquals("recovery failed", ((Try.Failure<String>) recovered).getCause().getMessage());
    }

    @Test
    void toString_shouldReturnCorrectFormat() {
        assertEquals("Success(hello)", Try.success("hello").toString());
        assertEquals("Failure(RuntimeException: Something went wrong)", Try.failure(new RuntimeException("Something went wrong")).toString());
    }

    @Test
    void foldOnSuccessShouldApplySuccessMapper() {
        Try<Integer> successTry = Try.of(() -> 10 / 2);

        String result = successTry.fold(
                value -> "Result: " + value,
                ex -> "Exception: " + ex.getClass().getSimpleName()
        );

        assertEquals("Result: 5", result);
    }

    @Test
    void foldOnFailureShouldApplyFailureMapper() {
        Try<Integer> failureTry = Try.of(() -> 10 / 0);

        String result = failureTry.fold(value -> "Result: " + value, ex -> "Exception: " + ex.getClass().getSimpleName());

        assertEquals("Exception: ArithmeticException", result);
    }
}