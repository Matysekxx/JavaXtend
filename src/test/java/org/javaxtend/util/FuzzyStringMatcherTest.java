package org.javaxtend.util;

import org.javaxtend.functional.Maybe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuzzyStringMatcherTest {

    private final List<String> options = List.of("commit", "push", "pull", "status", "branch");

    @Test
    @DisplayName("should find the correct match for a minor typo")
    void findBestMatch_findsCorrectMatch() {
        Maybe<String> match = FuzzyStringMatcher.findBestMatch("comit", options);
        assertTrue(match.isJust());
        assertEquals("commit", match.unwrap());
    }

    @Test
    @DisplayName("should find a match with a different case")
    void findBestMatch_isCaseSensitive() {
        Maybe<String> match = FuzzyStringMatcher.findBestMatch("Push", options);
        assertTrue(match.isJust());
        assertEquals("push", match.unwrap());
    }

    @Test
    @DisplayName("should return nothing if no good match is found")
    void findBestMatch_returnsNothingForNoGoodMatch() {
        Maybe<String> match = FuzzyStringMatcher.findBestMatch("checkout", options);
        assertTrue(match.isNothing(), "Should not find a match for a completely different word.");
    }

    @Test
    @DisplayName("should return nothing for empty or null input")
    void findBestMatch_handlesEmptyInput() {
        assertTrue(FuzzyStringMatcher.findBestMatch("", options).isNothing());
        assertTrue(FuzzyStringMatcher.findBestMatch(null, options).isNothing());
        assertTrue(FuzzyStringMatcher.findBestMatch("test", List.of()).isNothing());
    }
}