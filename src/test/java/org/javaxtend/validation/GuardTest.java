package org.javaxtend.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GuardTest {

    @Test
    void isNull_withNullInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().isNull(null, "testObject")
        );
    }

    @Test
    void isNull_withValidInput_doesNotThrow() {
        assertDoesNotThrow(() ->
                Guard.against().isNull(new Object(), "testObject")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void nullOrBlank_withInvalidInput_throwsException(String input) {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().nullOrBlank(input, "testParam")
        );
    }

    @Test
    void nullOrBlank_withValidInput_doesNotThrow() {
        assertDoesNotThrow(() ->
                Guard.against().nullOrBlank("valid", "testParam")
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    void negativeOrZero_withInvalidInput_throwsException(long input) {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().negativeOrZero(input, "testAmount")
        );
    }

    @Test
    void nullOrEmpty_withInvalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().nullOrEmpty((String) null, "testParam")
        );
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().nullOrEmpty("", "testParam")
        );
    }

    @Test
    void nullOrEmpty_withValidInput_doesNotThrow() {
        assertDoesNotThrow(() -> Guard.against().nullOrEmpty("valid", "testParam"));
        assertDoesNotThrow(() -> Guard.against().nullOrEmpty("   ", "testParam"));
    }

    @Test
    void negativeOrZero_withValidInput_doesNotThrow() {
        assertDoesNotThrow(() ->
                Guard.against().negativeOrZero(1, "testAmount")
        );
    }

    @Test
    void negative_withNegativeInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().negative(-1, "testAmount")
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 100})
    void negative_withZeroOrPositiveInput_doesNotThrow(long input) {
        assertDoesNotThrow(() ->
                Guard.against().negative(input, "testAmount")
        );
    }

    @Test
    void zero_withZeroInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().zero(0, "testAmount")
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {1, -1, 100})
    void zero_withNonZeroInput_doesNotThrow(long input) {
        assertDoesNotThrow(() ->
                Guard.against().zero(input, "testAmount")
        );
    }

    @Test
    void invalidFormat_withNonMatchingInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().invalidFormat("not-an-email", "\\S+@\\S+\\.\\S+", "email")
        );
    }

    @Test
    void invalidFormat_withMatchingInput_doesNotThrow() {
        assertDoesNotThrow(() ->
                Guard.against().invalidFormat("test@example.com", "\\S+@\\S+\\.\\S+", "email")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void outOfRange_withValueOutsideRange_throwsException(int input) {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().outOfRange(input, 1, 10, "testValue")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void outOfRange_withValueInsideRange_doesNotThrow(int input) {
        assertDoesNotThrow(() ->
                Guard.against().outOfRange(input, 1, 10, "testValue")
        );
    }


    @Test
    void nullOrEmpty_withNullCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().nullOrEmpty((String) null, "testList")
        );
    }

    @Test
    void nullOrEmpty_withEmptyCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Guard.against().nullOrEmpty(Collections.emptyList(), "testList")
        );
    }

    @Test
    void nullOrEmpty_withValidCollection_doesNotThrow() {
        assertDoesNotThrow(() ->
                Guard.against().nullOrEmpty(List.of("item"), "testList")
        );
    }
}