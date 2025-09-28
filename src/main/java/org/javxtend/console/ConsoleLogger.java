package org.javxtend.console;

import org.javxtend.io.IO;

public class ConsoleLogger {

    public enum LogLevel {
        DEBUG(1), INFO(2), WARN(3), ERROR(4);
        private final int priority;
        LogLevel(int priority) { this.priority = priority; }
        public int getPriority() { return priority; }
    }

    private static LogLevel currentLevel = LogLevel.DEBUG;

    public static void setLevel(LogLevel level) {
        currentLevel = level;
    }

    public static void debug(String msg) {
        log(LogLevel.DEBUG, ConsoleColors.CYAN + "[DEBUG]" + ConsoleColors.RESET + " " + msg);
    }

    public static void info(String msg) {
        log(LogLevel.INFO, ConsoleColors.GREEN + "[INFO]" + ConsoleColors.RESET + " " + msg);
    }

    public static void warn(String msg) {
        log(LogLevel.WARN, ConsoleColors.YELLOW + "[WARN]" + ConsoleColors.RESET + " " + msg);
    }

    public static void error(String msg) {
        log(LogLevel.ERROR, ConsoleColors.RED + "[ERROR]" + ConsoleColors.RESET + " " + msg);
    }

    private static void log(LogLevel level, String formatted) {
        if (level.getPriority() >= currentLevel.getPriority()) {
            IO.println(formatted);
        }
    }
}
