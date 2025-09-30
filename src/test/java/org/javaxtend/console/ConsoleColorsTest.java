package org.javaxtend.console;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleColorsTest {

    @Test
    @DisplayName("colorize() should wrap text with color and reset codes")
    void testColorize() {
        String text = "Hello";
        String expected = "\033[31mHello\033[0m";
        assertEquals(expected, ConsoleColors.RED.colorize(text));
    }

    @Test
    @DisplayName("toString() should return the raw ANSI code")
    void testToString() {
        assertEquals("\033[34m", ConsoleColors.BLUE.toString());
    }

    @Test
    @DisplayName("combine() should concatenate multiple ANSI codes")
    void testCombine() {
        String expected = "\033[1m\033[93m";
        String combined = ConsoleColors.combine(ConsoleColors.BOLD, ConsoleColors.YELLOW_BRIGHT);
        assertEquals(expected, combined);
    }
}