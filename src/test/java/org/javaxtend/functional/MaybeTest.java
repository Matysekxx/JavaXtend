package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MaybeTest {

    @Test
    void just_createsJustInstance() {
        Maybe<String> maybe = Maybe.just("test");
        assertTrue(maybe.isJust());
        assertFalse(maybe.isNothing());
    }

    @Test
    void just_throwsExceptionForNull() {
        assertThrows(NullPointerException.class, () -> Maybe.just(null));
    }

    @Test
    void nothing_createsNothingInstance() {
        Maybe<Integer> maybe = Maybe.nothing();
        assertTrue(maybe.isNothing());
        assertFalse(maybe.isJust());
    }

    @Test
    void ofNullable_createsJustForNonNull() {
        Maybe<String> maybe = Maybe.ofNullable("value");
        assertTrue(maybe.isJust());
    }

    @Test
    void ofNullable_createsNothingForNull() {
        Maybe<String> maybe = Maybe.ofNullable(null);
        assertTrue(maybe.isNothing());
    }

    @Test
    void ifJust_executesOnJust() {
        final boolean[] executed = {false};
        Maybe.just("data").ifJust(val -> {
            assertEquals("data", val);
            executed[0] = true;
        });
        assertTrue(executed[0]);
    }

    @Test
    void ifJust_doesNotExecuteOnNothing() {
        Maybe.nothing().ifJust(_ -> fail("Should not be executed"));
    }

    @Test
    void orElse_returnsValueOnJust() {
        assertEquals("hello", Maybe.just("hello").orElse("default"));
    }

    @Test
    void orElse_returnsDefaultOnNothing() {
        assertEquals("default", Maybe.nothing().orElse("default"));
    }

    @Test
    void unwrap_throwsExceptionOnNothing() {
        assertThrows(NoSuchElementException.class, () -> Maybe.nothing().unwrap());
    }

    @Test
    void map_transformsValueOnJust() {
        Maybe<Integer> length = Maybe.just("hello").map(String::length);
        assertTrue(length.isJust());
        assertEquals(5, length.unwrap());
    }

    @Test
    void map_doesNothingOnNothing() {
        Maybe<Integer> length = Maybe.<String>nothing().map(String::length);
        assertTrue(length.isNothing());
    }

    @Test
    void flatMap_chainsMaybeOnJust() {
        Maybe<Integer> result = Maybe.just("5")
                .flatMap(s -> {
                    try {
                        return Maybe.just(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        return Maybe.nothing();
                    }
                });
        assertEquals(5, result.unwrap());
    }

    @Test
    void flatMap_doesNothingOnNothing() {
        Maybe<Integer> result = Maybe.<String>nothing().flatMap(s -> Maybe.just(s.length()));
        assertTrue(result.isNothing());
    }
}