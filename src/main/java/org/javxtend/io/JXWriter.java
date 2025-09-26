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

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
