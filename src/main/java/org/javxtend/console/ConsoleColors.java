package org.javxtend.console;

/**
 * An enumeration of ANSI escape codes for styling text in a console.
 * <p>
 * This enum provides a simple way to add colors, background colors, and text styles
 * like bold and underline to console output. It is useful for creating more
 * readable and visually appealing command-line applications.
 * <p>
 * Note: The rendering of these colors depends on the capabilities of the
 * terminal or console where the application is run. Not all terminals support
 * all colors or styles.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Simple colorization
 *     System.out.println(ConsoleColors.RED.colorize("This is an error message."));
 *
 *     // Combining styles
 *     String boldYellow = ConsoleColors.combine(ConsoleColors.BOLD, ConsoleColors.YELLOW);
 *     System.out.println(boldYellow + "This is a warning." + ConsoleColors.RESET);
 * </pre></blockquote>
 */
public enum ConsoleColors {

    RESET("\033[0m"),
    BOLD("\033[1m"),
    UNDERLINE("\033[4m"),
    BLACK("\033[30m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    MAGENTA("\033[35m"),
    CYAN("\033[36m"),
    WHITE("\033[37m"),
    BLACK_BRIGHT("\033[90m"),
    RED_BRIGHT("\033[91m"),
    GREEN_BRIGHT("\033[92m"),
    YELLOW_BRIGHT("\033[93m"),
    BLUE_BRIGHT("\033[94m"),
    MAGENTA_BRIGHT("\033[95m"),
    CYAN_BRIGHT("\033[96m"),
    WHITE_BRIGHT("\033[97m"),
    BG_BLACK("\033[40m"),
    BG_RED("\033[41m"),
    BG_GREEN("\033[42m"),
    BG_YELLOW("\033[43m"),
    BG_BLUE("\033[44m"),
    BG_MAGENTA("\033[45m"),
    BG_CYAN("\033[46m"),
    BG_WHITE("\033[47m"),
    BG_BLACK_BRIGHT("\033[100m"),
    BG_RED_BRIGHT("\033[101m"),
    BG_GREEN_BRIGHT("\033[102m"),
    BG_YELLOW_BRIGHT("\033[103m"),
    BG_BLUE_BRIGHT("\033[104m"),
    BG_MAGENTA_BRIGHT("\033[105m"),
    BG_CYAN_BRIGHT("\033[106m"),
    BG_WHITE_BRIGHT("\033[107m");

    /**
     * The raw ANSI escape code string.
     */
    private final String ANSI_COLOR;

    /**
     * Constructs a new console color with its corresponding ANSI code.
     *
     * @param ansiColor The ANSI escape code string.
     */
    ConsoleColors(String ansiColor) {
        this.ANSI_COLOR = ansiColor;
    }

    /**
     * Wraps the given text with this color's ANSI code and a reset code.
     * <p>
     * This ensures that only the specified text is colored and that subsequent
     * text in the console returns to the default style.
     *
     * @param text The text to be colorized.
     * @return The colorized text string.
     */
    public String colorize(String text) {
        return ANSI_COLOR + text + RESET.ANSI_COLOR;
    }

    /**
     * Combines multiple {@code ConsoleColors} into a single ANSI sequence.
     * <p>
     * This is useful for applying multiple styles at once, such as making text
     * both bold and a specific color. The resulting string should be followed by
     * {@link #RESET} to clear the formatting.
     *
     * @param colors A varargs array of {@code ConsoleColors} to combine.
     * @return A single string containing all the specified ANSI codes.
     * @see #colorize(String)
     */
    public static String combine(ConsoleColors... colors) {
        final StringBuilder sb = new StringBuilder();
        for (ConsoleColors color : colors)
            sb.append(color.ANSI_COLOR);
        return sb.toString();
    }

    /**
     * Returns the raw ANSI escape code for this color.
     * <p>
     * This is equivalent to directly accessing the internal code and allows the enum
     * constant to be used directly in string concatenations.
     *
     * @return The ANSI escape code as a string.
     */
    @Override
    public String toString() {
        return ANSI_COLOR;
    }
}
