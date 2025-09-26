package org.javxtend;

import org.javxtend.io.IO;

public class Main {
    public static void main(String[] args) {
        IO.println("Please enter a number, a word, and then the rest of the line:");
        int number = IO.nextInt();
        String word = IO.next();
        String line = IO.nextLine();
        
        IO.println("Number: " + number);
        IO.println("Word: " + word);
        IO.println("Line: " + line);
    }
}