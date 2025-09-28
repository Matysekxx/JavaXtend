package org.javxtend.console;

import org.javxtend.io.JXScanner;

import java.util.Scanner;
import java.util.function.Predicate;

public class ConsoleInput {
    private static final JXScanner scanner = new JXScanner(System.in);
    public static String readString(String prompt) {
        IO.print(prompt);
        return scanner.nextLine();
    }

    public static String readString(String prompt, Predicate<String> validator) {
        while (true) {
            IO.println(prompt);
            String input = scanner.nextLine();
            if (validator.test(input)) {
                return input;
            }
            IO.println("Invalid input, try again.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            IO.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                IO.println("Please enter a valid integer.");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            IO.println("Value must be between " + min + " and " + max + ".");
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            IO.print(prompt);
            String input = scanner.nextLine();
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                IO.println("Please enter a valid number.");
            }
        }
    }

    public static double readDouble(String prompt, Predicate<Double> validator) {
        while (true) {
            double value = readDouble(prompt);
            if (validator.test(value)) {
                return value;
            }
            IO.println("Invalid value, try again.");
        }
    }

    public static boolean readYesNo(String prompt) {
        return readYesNo(prompt, false);
    }

    public static boolean readYesNo(String prompt, boolean defaultValue) {
        while (true) {
            IO.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "" -> {
                    return defaultValue;
                }
                case "y", "yes" -> {
                    return true;
                }
                case "n", "no" -> {
                    return false;
                }
            }
            IO.println("Please enter 'y' or 'n'.");
        }
    }
}
