package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchTest {

    @Test
    void when_matchesFirstApplicableType() {
        Object value = "test string";
        String result = Match.of(value)
                .when(Integer.class, i -> "Integer")
                .when(String.class, s -> "String: " + s)
                .when(List.class, l -> "List")
                .orElse("Default")
                .get()
                .toString();

        assertEquals("String: test string", result);
    }

    @Test
    void when_withGuard_matchesCorrectly() {
        Object value = "a long test string";
        String result = Match.of(value)
                .when(String.class, s -> s.length() < 5, s -> "Short string")
                .when(String.class, s -> s.length() > 10, s -> "Long string")
                .orElse("Medium string")
                .get()
                .toString();

        assertEquals("Long string", result);
    }

    @Test
    void when_withGuard_skipsNonMatchingGuard() {
        Object value = "medium";
        String result = Match.of(value)
                .when(String.class, s -> s.length() < 5, s -> "Short string")
                .when(String.class, s -> s.length() > 10, s -> "Long string")
                .orElse("Medium string")
                .get()
                .toString();

        assertEquals("Medium string", result);
    }

    @Test
    void when_stopsAtFirstMatch() {
        Object value = "stop here";
        String result = Match.of(value)
                .when(String.class, s -> "First match")
                .when(String.class, s -> "Second match, should not be reached")
                .orElse("Default")
                .get()
                .toString();

        assertEquals("First match", result);
    }

    @Test
    void orElse_isUsedWhenNoMatchFound() {
        Object value = 123.45;
        String result = Match.of(value)
                .when(Integer.class, i -> "Integer")
                .when(String.class, s -> "String")
                .orElse("Default value")
                .get()
                .toString();

        assertEquals("Default value", result);
    }

    @Test
    void when_matchesSpecificValue() {
        Object value = 42;
        String result = Match.of(value)
                .when(10, () -> "Ten")
                .when(42, () -> "The answer")
                .when(Integer.class, i -> "Some other integer")
                .orElse("Default")
                .get()
                .toString();

        assertEquals("The answer", result);
    }
}