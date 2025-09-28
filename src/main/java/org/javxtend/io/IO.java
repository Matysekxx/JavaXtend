package org.javxtend.io;

import org.javxtend.console.ConsoleColors;
import org.javxtend.util.JXPair;
import org.javxtend.util.JXTriple;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * A utility class providing static methods for convenient console I/O.
 * <p>
 * This class uses a single, shared instance of {@link JXScanner} to read from
 * {@code System.in} and a {@link PrintWriter} to write to {@code System.out}.
 * It is designed for rapid development and competitive programming scenarios.
 * <p>
 * For more flexible I/O (e.g., reading from files), create a new instance of
 * {@code JXScanner} directly.
 */
public final class IO {

    /** The shared scanner instance for reading from the configured input stream. */
    private static JXScanner SCANNER = new JXScanner(System.in);
    /** The shared writer instance for writing to the configured output stream. */
    private static JXWriter WRITER = new JXWriter(System.out, true);

    /**
     * Private constructor to prevent instantiation of this utility class.
     * <p>
     * The methods in this class are static and operate on shared {@code JXScanner}
     * and {@code JXWriter} instances.
     * </p>
     */
    private IO() {}

    public static void initialize(InputStream in, OutputStream out) {
        SCANNER = new JXScanner(in);
        WRITER = new JXWriter(out);
    }

    public static void print(Object obj) {
        WRITER.print(obj);
    }

    public static void printERR(Object obj) {
        WRITER.print(ConsoleColors.RED.colorize(obj.toString()));
    }

    /**
     * Prints an object to the console and ensures it is immediately visible.
     * <p>
     * This is intended for interactive output like spinners or progress bars that
     * update on a single line. It automatically flushes the output stream.
     * @param obj The object to be printed.
     */
    public static void printLive(Object obj) {
        WRITER.printAndFlush(obj);
    }

    public static void println(Object obj) {
        WRITER.println(obj);
    }

    public static void println() {
        WRITER.println();
    }

    public static void printlnERR(Object obj) {
        WRITER.println(ConsoleColors.RED.colorize(obj.toString()));
    }

    public static void flush() {
        WRITER.flush();
    }

    public static String next() {
        return SCANNER.next();
    }

    public static String nextLine() {
        return SCANNER.nextLine();
    }

    public static int nextInt() {
        return SCANNER.nextInt();
    }

    public static long nextLong() {
        return SCANNER.nextLong();
    }

    public static double nextDouble() {
        return SCANNER.nextDouble();
    }

    public static int[] nextInts(int count) {
        return SCANNER.nextInts(count);
    }

    public static List<String> nexts(int count) {
        return SCANNER.nexts(count);
    }

    public static JXPair<Integer, Integer> nextIntPair() {
        return SCANNER.nextIntPair();
    }

    public static JXTriple<Integer, Integer, Integer> nextIntTriple() {
        return SCANNER.nextIntTriple();
    }

    public static boolean hasNext() {
        return SCANNER.hasNext();
    }
}