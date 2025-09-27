package org.javxtend.io;

import org.javxtend.util.JXTuple;
import org.javxtend.util.JXTriple;

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

    private static final JXScanner SCANNER = new JXScanner(System.in);
    private static final JXWriter WRITER = new JXWriter(System.out, true);
    private IO() {}

    public static void print(Object obj) {
        WRITER.print(obj);
    }

    public static void println(Object obj) {
        WRITER.println(obj);
    }

    public static void println() {
        WRITER.println();
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

    public static JXTuple<Integer, Integer> nextIntTuple() {
        return SCANNER.nextIntTuple();
    }

    public static JXTriple<Integer, Integer, Integer> nextIntTriple() {
        return SCANNER.nextIntTriple();
    }

    public static boolean hasNext() {
        return SCANNER.hasNext();
    }
}