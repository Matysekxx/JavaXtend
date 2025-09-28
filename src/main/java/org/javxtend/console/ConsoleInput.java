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

    /**
     * Reads an integer from the console after displaying a prompt.
     * <p>
     * It will continue to prompt the user until a valid integer is entered.
     * @param prompt The message to display to the user.
     * @return The integer entered by the user.
     */
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

    /**
     * Reads an integer within a specified range from the console.
     * <p>
     * It will continue to prompt the user until a valid integer within the range [min, max] is entered.
     * @param prompt The message to display to the user.
     * @param min The minimum acceptable value (inclusive).
     * @param max The maximum acceptable value (inclusive).
     * @return The valid integer entered by the user.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            IO.println(ConsoleColors.RED.colorize("Value must be between " + min + " and " + max + "."));
        }
    }

    /**
     * Reads a double from the console after displaying a prompt.
     * <p>
     * It will continue to prompt the user until a valid double is entered.
     * @param prompt The message to display to the user.
     * @return The double entered by the user.
     */
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

    /**
     * Reads a double and validates it using a predicate.
     * <p>
     * It will continue to prompt the user until a valid double is entered.
     * @param prompt The message to display to the user.
     * @param validator A predicate to test the validity of the input.
     * @return The valid double entered by the user.
     */
    public static double readDouble(String prompt, Predicate<Double> validator) {
        while (true) {
            double value = readDouble(prompt);
            if (validator.test(value)) {
                return value;
            }
            IO.println(ConsoleColors.RED.colorize("Invalid value, please try again."));
        }
    }

    /**
     * Prompts the user for a "yes" or "no" answer.
     * <p>
     * This method is case-insensitive and accepts "y", "yes", "n", or "no".
     * The default value if the user just presses Enter is 'no'.
     * @param prompt The question to ask the user.
     * @return {@code true} for "yes", {@code false} for "no".
     */
    public static boolean readYesNo(String prompt) {
        return readYesNo(prompt, false);
    }

    /**
     * Prompts the user for a "yes" or "no" answer, with a specified default value.
     * <p>
     * This method is case-insensitive and accepts "y", "yes", "n", or "no".
     * If the user just presses Enter, the {@code defaultValue} is returned.
     * @param prompt The question to ask the user.
     * @param defaultValue The value to return if the user provides empty input.
     * @return {@code true} for "yes", {@code false} for "no".
     */
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
