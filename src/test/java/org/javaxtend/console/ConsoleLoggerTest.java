package org.javaxtend.console;

import org.javaxtend.io.IO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleLoggerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        IO.initialize(System.in, System.out);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        IO.initialize(System.in, System.out);
    }

    @Test
    void testLogLevelFiltering() {
        ConsoleLogger.setLevel(ConsoleLogger.LogLevel.INFO);
        ConsoleLogger.debug("This is a debug message.");
        assertFalse(outContent.toString().contains("[DEBUG]"), "Debug message should not be logged at INFO level.");

        ConsoleLogger.info("This is an info message.");
        assertTrue(outContent.toString().contains("[INFO]"), "Info message should be logged at INFO level.");

        ConsoleLogger.warn("This is a warn message.");
        assertTrue(outContent.toString().contains("[WARN]"), "Warn message should be logged at INFO level.");

        ConsoleLogger.error("This is an error message.");
        assertTrue(outContent.toString().contains("[ERROR]"), "Error message should be logged at INFO level.");
    }

    @Test
    void testMessageFormatting() {
        ConsoleLogger.setLevel(ConsoleLogger.LogLevel.DEBUG);

        ConsoleLogger.info("Test message");
        String output = outContent.toString();

        assertTrue(output.contains(ConsoleColors.GREEN.toString()), "INFO message should be green.");
        assertTrue(output.contains("[INFO]"), "Message should contain level prefix.");
        assertTrue(output.contains("Test message"), "Message should contain the original text.");
        assertTrue(output.contains(ConsoleColors.RESET.toString()), "Message should contain a reset code.");
    }

    @Test
    void testSetLevelChangesBehavior() {
        ConsoleLogger.setLevel(ConsoleLogger.LogLevel.ERROR);
        ConsoleLogger.warn("Warning should not be visible.");
        assertTrue(outContent.toString().isEmpty(), "Only ERROR messages should be logged.");
    }
}