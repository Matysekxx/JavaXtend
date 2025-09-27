package org.javxtend.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.javxtend.util.JXTuple;
import org.javxtend.util.JXTriple;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A simple text scanner which can parse primitive types and strings using
 * regular expressions. It is designed for convenience and performance,
 * especially in competitive programming or for quick prototyping.
 * <p>
 * A {@code JXScanner} breaks its input into tokens using a delimiter
 * pattern, which by default matches whitespace. The resulting tokens may then be
 * converted into values of different types using the various {@code next}
 * methods.
 * <p>
 * This class offers additional convenience methods not found in
 * {@link java.util.Scanner}, such as reading multiple values into arrays,
 * lists, or tuples in a single call.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Read from a string for testing
 *     String input = "10 20\nhello world";
 *     try (JXScanner scanner = new JXScanner(new ByteArrayInputStream(input.getBytes()))) {
 *         // Read a tuple of two integers
 *         JXTuple&lt;Integer, Integer&gt; point = scanner.nextIntTuple(); // (10, 20)
 *
 *         // Read the next line
 *         String line = scanner.nextLine(); // "hello world"
 *     }
 * </pre></blockquote>
 */
public class JXScanner implements Closeable {

    private final BufferedReader reader;
    private String[] buffer;
    private int pointer;
    private boolean inputExhausted = false;

    /**
     * Constructs a new {@code JXScanner} that produces values scanned from the
     * specified input stream.
     *
     * @param source An input stream to be scanned.
     */
    public JXScanner(InputStream source) {
        this.reader = new BufferedReader(new InputStreamReader(source));
        this.buffer = new String[0];
        this.pointer = 0;
    }

    /**
     * Constructs a new {@code JXScanner} that produces values scanned
     * from the standard input stream (System.in).
     */
    public JXScanner() {
        this(System.in);
    }

    /**
     * Advances this scanner past the current line and returns the input that was skipped.
     *
     * @return the line that was skipped.
     * @throws NoSuchElementException if no line was found.
     */
    public String nextLine() {
        if (pointer < buffer.length) {
            final StringBuilder restOfLine = new StringBuilder();
            for (int i = pointer; i < buffer.length; i++) {
                restOfLine.append(buffer[i]).append(" ");
            }
            reset();
            return restOfLine.toString().trim();
        }
        reset();
        return readLineFromStream();
    }

    private String readLineFromStream() {
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new NoSuchElementException("No more lines available");
            }
            return line;
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read next line due to an I/O error.");
        }
    }

    /**
     * Finds and returns the next complete token from this scanner.
     *
     * @return the next token.
     * @throws NoSuchElementException if no more tokens are available.
     */
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more tokens available.");
        }
        return buffer[pointer++];
    }

    /**
     * Scans the next token of the input as an {@code int}.
     *
     * @return the {@code int} scanned from the input.
     * @throws NoSuchElementException if the input is exhausted.
     * @throws NumberFormatException if the next token does not match the Integer regular expression, or is out of range.
     */
    public int nextInt() {
        return Integer.parseInt(next());
    }

    /**
     * Scans the next token of the input as a {@code double}.
     *
     * @return the {@code double} scanned from the input.
     * @throws NoSuchElementException if the input is exhausted.
     * @throws NumberFormatException if the next token does not match the Double regular expression, or is out of range.
     */
    public double nextDouble() {
        return Double.parseDouble(next());
    }

    /**
     * Scans the next token of the input as a {@code long}.
     *
     * @return the {@code long} scanned from the input.
     * @throws NoSuchElementException if the input is exhausted.
     * @throws NumberFormatException if the next token does not match the Long regular expression, or is out of range.
     */
    public long nextLong() {
        return Long.parseLong(next());
    }

    /**
     * Scans the next token of the input as a {@code float}.
     *
     * @return the {@code float} scanned from the input.
     * @throws NoSuchElementException if the input is exhausted.
     * @throws NumberFormatException if the next token does not match the Float regular expression, or is out of range.
     */
    public float nextFloat() {
        return Float.parseFloat(next());
    }

    /**
     * Scans the next token of the input as a {@code boolean}.
     *
     * @return the {@code boolean} scanned from the input.
     * @throws NoSuchElementException if the input is exhausted.
     */
    public boolean nextBoolean() {
        return Boolean.parseBoolean(next());
    }

    /**
     * Scans the next token and returns its first character.
     *
     * @return the first {@code char} of the next token.
     * @throws NoSuchElementException if the input is exhausted.
     */
    public char nextChar() {
        return next().charAt(0);
    }
    
    /**
     * Reads the next {@code count} tokens as integers and returns them as an array.
     * @param count the number of integers to read.
     * @return an array of integers.
     * @throws NoSuchElementException if there are not enough tokens.
     */
    public int[] nextInts(int count) {
        int[] array = new int[count];
        for (int i = 0; i < count; i++) {
            array[i] = nextInt();
        }
        return array;
    }

    /**
     * Reads the next {@code count} tokens as longs and returns them as an array.
     * @param count the number of longs to read.
     * @return an array of longs.
     * @throws NoSuchElementException if there are not enough tokens.
     */
    public long[] nextLongs(int count) {
        long[] array = new long[count];
        for (int i = 0; i < count; i++) {
            array[i] = nextLong();
        }
        return array;
    }

    /**
     * Reads the next {@code count} tokens as doubles and returns them as an array.
     * @param count the number of doubles to read.
     * @return an array of doubles.
     * @throws NoSuchElementException if there are not enough tokens.
     */
    public double[] nextDoubles(int count) {
        double[] array = new double[count];
        for (int i = 0; i < count; i++) {
            array[i] = nextDouble();
        }
        return array;
    }

    /**
     * Reads the next {@code count} tokens as strings and returns them as a list.
     * @param count the number of strings to read.
     * @return a list of strings.
     * @throws NoSuchElementException if there are not enough tokens.
     */
    public List<String> nexts(int count) {
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(next());
        }
        return list;
    }

    /**
     * Reads the next two tokens and returns them as a mutable {@link JXTuple}.
     * @return a {@code JXTuple} containing the next two strings.
     */
    public JXTuple<String, String> nextTuple() {
        return JXTuple.of(next(), next());
    }

    /**
     * Reads the next two tokens as integers and returns them as a mutable {@link JXTuple}.
     * @return a {@code JXTuple} containing the next two integers.
     */
    public JXTuple<Integer, Integer> nextIntTuple() {
        return JXTuple.of(nextInt(), nextInt());
    }

    /**
     * Reads the next three tokens and returns them as a mutable {@link JXTriple}.
     * @return a {@code JXTriple} containing the next three strings.
     */
    public JXTriple<String, String, String> nextTriple() {
        return JXTriple.of(next(), next(), next());
    }

    /**
     * Reads the next three tokens as integers and returns them as a mutable {@link JXTriple}.
     * @return a {@code JXTriple} containing the next three integers.
     */
    public JXTriple<Integer, Integer, Integer> nextIntTriple() {
        return JXTriple.of(nextInt(), nextInt(), nextInt());
    }

    /**
     * Returns true if there is another token in this scanner's input.
     * This method may block while waiting for input to scan.
     *
     * @return true if and only if this scanner has another token.
     */
    public boolean hasNext() {
        if (inputExhausted) return false;
        while (pointer >= buffer.length) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    inputExhausted = true;
                    return false;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                buffer = line.trim().split("\\s+");
            } catch (IOException e) {
                return false;
            }
            pointer = 0;
        }
        return true;
    }

    /**
     * Resets the scanner's internal buffer.
     * Typically used before a call to {@code nextLine()} to clear any remaining tokens from the current line.
     */
    public void reset() {
        buffer = new String[0];
        pointer = 0;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
