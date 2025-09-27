package org.javxtend.io;

import org.javxtend.util.JXPair;
import org.javxtend.util.JXTriple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class JXScannerTest {

    private JXScanner createScanner(String data) {
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        return new JXScanner(inputStream);
    }

    @Test
    @DisplayName("Should read basic types correctly")
    void testNextPrimitives() {
        try (JXScanner scanner = createScanner("hello 123 45.6 true")) {
            assertEquals("hello", scanner.next());
            assertEquals(123, scanner.nextInt());
            assertEquals(45.6, scanner.nextDouble());
            assertTrue(scanner.nextBoolean());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Should read an array of integers")
    void testNextInts() {
        try (JXScanner scanner = createScanner("10 20 30 40")) {
            int[] numbers = scanner.nextInts(4);
            assertArrayEquals(new int[]{10, 20, 30, 40}, numbers);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Should read a list of strings")
    void testNexts() {
        try (JXScanner scanner = createScanner("one two three")) {
            List<String> words = scanner.nexts(3);
            assertEquals(List.of("one", "two", "three"), words);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Should read integer tuples correctly")
    void testNextIntTuples() {
        try (JXScanner scanner = createScanner("1 2 10 20 30")) {
            JXPair<Integer, Integer> pair = scanner.nextIntPair();
            assertEquals(JXPair.of(1, 2), pair);

            JXTriple<Integer, Integer, Integer> triple = scanner.nextIntTriple();
            assertEquals(JXTriple.of(10, 20, 30), triple);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Should handle nextLine correctly after next")
    void testNextLineBehavior() {
        try (JXScanner scanner = createScanner("first second\nthird line")) {
            scanner.next();
            String line = scanner.nextLine();
            assertEquals("second", line.trim());

            String nextLine = scanner.nextLine();
            assertEquals("third line", nextLine);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Should throw NoSuchElementException for insufficient input")
    void testInsufficientInput() {
        try (JXScanner scanner = createScanner("only one token")) {
            scanner.next();
            scanner.next();
            scanner.next();
            assertThrows(NoSuchElementException.class, scanner::next);
        } catch (IOException e) {
            fail(e);
        }
    }
}