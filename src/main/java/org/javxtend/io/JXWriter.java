package org.javxtend.io;

import java.io.*;
import java.util.Collection;
import java.util.StringJoiner;

/**
 * A utility class for writing text to a character-output stream, providing
 * enhanced convenience methods for common formatting tasks.
 * <p>
 * This class wraps a standard {@link PrintWriter} and adds methods for easily
 * printing collections, arrays, and other data structures with custom delimiters.
 * It is designed to reduce boilerplate code in scenarios like competitive
 * programming or data serialization.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     try (JXWriter writer = new JXWriter()) { // Defaults to System.out
 *         int[] numbers = {1, 2, 3, 4, 5};
 *         writer.printlnArray(numbers, " -> "); // Prints: 1 -> 2 -> 3 -> 4 -> 5
 *     }
 * </pre></blockquote>
 */
public class JXWriter implements Closeable, Flushable {

    private final PrintWriter writer;

    /**
     * Creates a new JXWriter, without automatic line flushing, from an
     * existing OutputStream.
     *
     * @param out An output stream
     */
    public JXWriter(OutputStream out) {
        this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
    }

    /**
     * Creates a new JXWriter that writes to the standard output stream (System.out).
     * Auto-flushing is enabled.
     */
    public JXWriter() {
        this.writer = new PrintWriter(System.out, true);
    }

    public JXWriter(PrintStream out, boolean b) {
        this.writer = new PrintWriter(out, b);
    }

    public void print(Object obj) {
        writer.print(obj);
    }

    /**
     * Prints an object and then immediately flushes the stream.
     * <p>
     * This method is useful for interactive console applications (e.g., progress bars, spinners)
     * where immediate feedback is required without printing a new line.
     * @param obj The {@code Object} to be printed.
     */
    public void printAndFlush(Object obj) {
        writer.print(obj);
        writer.flush();
    }

    public void println(Object obj) {
        writer.println(obj);
    }

    public void println() {
        writer.println();
    }

    public JXWriter printf(String format, Object... args) {
        writer.printf(format, args);
        return this;
    }

    /**
     * Prints the elements of a collection to the output stream, separated by a delimiter.
     *
     * @param collection The collection to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printCollection(Collection<?> collection, CharSequence delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object item : collection) {
            joiner.add(String.valueOf(item));
        }
        print(joiner.toString());
    }

    /**
     * Prints the elements of a collection to the output stream, separated by a delimiter,
     * and then terminates the line.
     *
     * @param collection The collection to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printlnCollection(Collection<?> collection, CharSequence delimiter) {
        printCollection(collection, delimiter);
        println();
    }

    /**
     * Prints the elements of an array to the output stream, separated by a delimiter.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printArray(Object[] array, CharSequence delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object item : array) {
            joiner.add(String.valueOf(item));
        }
        print(joiner.toString());
    }

    /**
     * Prints the elements of an array to the output stream, separated by a delimiter,
     * and then terminates the line.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printlnArray(Object[] array, CharSequence delimiter) {
        printArray(array, delimiter);
        println();
    }

    /**
     * Prints the elements of an int array to the output stream, separated by a delimiter.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printArray(int[] array, CharSequence delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (int item : array) {
            joiner.add(String.valueOf(item));
        }
        print(joiner.toString());
    }

    /**
     * Prints the elements of an int array to the output stream, separated by a delimiter,
     * and then terminates the line.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printlnArray(int[] array, CharSequence delimiter) {
        printArray(array, delimiter);
        println();
    }

    /**
     * Prints the elements of a long array to the output stream, separated by a delimiter.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printArray(long[] array, CharSequence delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (long item : array) {
            joiner.add(String.valueOf(item));
        }
        print(joiner.toString());
    }

    /**
     * Prints the elements of a long array to the output stream, separated by a delimiter,
     * and then terminates the line.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printlnArray(long[] array, CharSequence delimiter) {
        printArray(array, delimiter);
        println();
    }

    /**
     * Prints the elements of a double array to the output stream, separated by a delimiter.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printArray(double[] array, CharSequence delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (double item : array) {
            joiner.add(String.valueOf(item));
        }
        print(joiner.toString());
    }

    /**
     * Prints the elements of a double array to the output stream, separated by a delimiter,
     * and then terminates the line.
     *
     * @param array The array to print.
     * @param delimiter The delimiter to place between elements.
     */
    public void printlnArray(double[] array, CharSequence delimiter) {
        printArray(array, delimiter);
        println();
    }

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
