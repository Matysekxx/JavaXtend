package org.javxtend.console;

import org.javxtend.io.IO;

/**
 * A simple, static logger for printing colored, level-based messages to the console.
 * <p>
 * It provides four standard logging levels: DEBUG, INFO, WARN, and ERROR. The current
 * logging level can be set globally to filter out less important messages.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Set the minimum level of messages to display
 *     ConsoleLogger.setLevel(ConsoleLogger.LogLevel.INFO);
 *
 *     ConsoleLogger.debug("This message will not be shown.");
 *     ConsoleLogger.info("Application starting...");
 *     ConsoleLogger.warn("Configuration is missing a value.");
 *     ConsoleLogger.error("Failed to connect to the database.");
 * </pre></blockquote>
 */
public class ConsoleLogger {

    /**
     * Defines the logging levels and their priority.
     */
    public enum LogLevel {
        DEBUG(1), INFO(2), WARN(3), ERROR(4);
        private final int priority;
        LogLevel(int priority) { this.priority = priority; }
        private int getPriority() { return priority; }
    }

    private static LogLevel currentLevel = LogLevel.DEBUG;

    private ConsoleLogger() {}

    /**
     * Sets the global logging level.
     * <p>
     * Only messages with a level of this priority or higher will be printed.
     * @param level The minimum log level to display.
     */
    public static void setLevel(LogLevel level) {
        currentLevel = level;
    }

    /**
     * Logs a message at the DEBUG level.
     * @param msg The message to log.
     */
    public static void debug(String msg) {
        if (LogLevel.DEBUG.getPriority() >= currentLevel.getPriority()) {
            IO.println(ConsoleColors.CYAN + "[DEBUG]" + ConsoleColors.RESET + " " + msg);
        }
    }

    /**
     * Logs a message at the INFO level.
     * @param msg The message to log.
     */
    public static void info(String msg) {
        if (LogLevel.INFO.getPriority() >= currentLevel.getPriority()) {
            IO.println(ConsoleColors.GREEN + "[INFO]" + ConsoleColors.RESET + " " + msg);
        }
    }

    /**
     * Logs a message at the WARN level.
     * @param msg The message to log.
     */
    public static void warn(String msg) {
        if (LogLevel.WARN.getPriority() >= currentLevel.getPriority()) {
            IO.println(ConsoleColors.YELLOW + "[WARN]" + ConsoleColors.RESET + " " + msg);
        }
    }

    /**
     * Logs a message at the ERROR level.
     * @param msg The message to log.
     */
    public static void error(String msg) {
        if (LogLevel.ERROR.getPriority() >= currentLevel.getPriority()) {
            IO.println(ConsoleColors.RED + "[ERROR]" + ConsoleColors.RESET + " " + msg);
        }
    }
}
