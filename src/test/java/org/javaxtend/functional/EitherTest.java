package org.javaxtend.functional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void left_createsLeftInstance() {
        Either<String, Integer> either = Either.left("Error");
        assertTrue(either.isLeft());
        assertFalse(either.isRight());
    }

    @Test
    void right_createsRightInstance() {
        Either<String, Integer> either = Either.right(123);
        assertTrue(either.isRight());
        assertFalse(either.isLeft());
    }

    @Test
    void left_throwsExceptionForNull() {
        assertThrows(NullPointerException.class, () -> Either.left(null));
    }

    @Test
    void right_throwsExceptionForNull() {
        assertThrows(NullPointerException.class, () -> Either.right(null));
    }

    @Test
    void fold_appliesLeftMapperOnLeft() {
        Either<String, Integer> either = Either.left("Error 404");
        String result = either.fold(
                leftValue -> "Handled Left: " + leftValue,
                rightValue -> "Handled Right: " + rightValue
        );
        assertEquals("Handled Left: Error 404", result);
    }

    @Test
    void fold_appliesRightMapperOnRight() {
        Either<String, Integer> either = Either.right(200);
        String result = either.fold(
                leftValue -> "Handled Left: " + leftValue,
                rightValue -> "Handled Right: " + rightValue
        );
        assertEquals("Handled Right: 200", result);
    }

    @Test
    void getValue_returnsCorrectValue() {
        Either<String, Integer> leftEither = Either.left("Left Value");
        if (leftEither instanceof Either.Left<String, Integer> left) {
            assertEquals("Left Value", left.getValue());
        } else {
            fail("Should be an instance of Left");
        }

        Either<String, Integer> rightEither = Either.right(99);
        if (rightEither instanceof Either.Right<String, Integer> right) {
            assertEquals(99, right.getValue());
        } else {
            fail("Should be an instance of Right");
        }
    }
}