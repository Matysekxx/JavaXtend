package org.javxtend.console;

import org.javxtend.io.IO;

import java.util.function.Predicate;

/**
 * A utility class providing static methods for robustly reading user input from the console.
 * <p>
 * This class handles prompting the user, reading input, validation, and re-prompting
 * on invalid input for various data types.
 */
public class ConsoleInput {
    private ConsoleInput() {}

    /**
     * Reads a line of text from the console after displaying a prompt.
     * @param prompt The message to display to the user.
     * @return The string entered by the user.
     */
    public static String readString(String prompt) {
        IO.print(prompt);
        return IO.nextLine();
    }

    /**
     * Reads a line of text and validates it using a predicate.
     * <p>
     * It will continue to prompt the user until a valid string is entered.
     * @param prompt The message to display to the user.
     * @param validator A predicate to test the validity of the input.
     * @return The valid string entered by the user.
     */
    public static String readString(String prompt, Predicate<String> validator) {
        while (true) {
            IO.print(prompt);
            String input = IO.nextLine();
            if (validator.test(input)) {
                return input;
            }
            IO.println(ConsoleColors.RED.colorize("Invalid input, please try again."));
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            IO.print(prompt);
            String input = IO.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                IO.println(ConsoleColors.RED.colorize("Invalid input. Please enter a valid integer."));
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            IO.println(ConsoleColors.RED.colorize("Value must be between " + min + " and " + max + "."));
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            IO.print(prompt);
            String input = IO.nextLine();
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                IO.println(ConsoleColors.RED.colorize("Invalid input. Please enter a valid number."));
            }
        }
    }

    public static double readDouble(String prompt, Predicate<Double> validator) {
        while (true) {
            double value = readDouble(prompt);
            if (validator.test(value)) {
                return value;
            }
            IO.println(ConsoleColors.RED.colorize("Invalid value, please try again."));
        }
    }

    public static boolean readYesNo(String prompt) {
        return readYesNo(prompt, false);
    }

    public static boolean readYesNo(String prompt, boolean defaultValue) {
        while (true) {
            String hint = defaultValue ? " [Y/n]" : " [y/N]";
            IO.print(prompt + hint + " ");
            String input = IO.nextLine().trim().toLowerCase();

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
            IO.println(ConsoleColors.RED.colorize("Please enter 'y' (yes) or 'n' (no)."));
        }
    }
}
