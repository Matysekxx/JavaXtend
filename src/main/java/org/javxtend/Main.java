package org.javxtend;

import org.javxtend.io.IO;
import org.javxtend.util.ImmutableTriple;

public class Main {
    public static void main(String[] args) {
        IO.println("Please enter a number, a word, and then the rest of the line:");
        int number = IO.nextInt();
        String word = IO.next();
        String line = IO.nextLine();
    }
}