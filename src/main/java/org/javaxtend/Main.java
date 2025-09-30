package org.javaxtend;

import org.javaxtend.console.ConsoleColors;
import org.javaxtend.console.ConsoleForm;
import org.javaxtend.console.ConsoleTable;
import org.javaxtend.functional.Result;
import org.javaxtend.util.JXTriple;

import java.util.Collection;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        var a = new ConsoleTable()
                .addRow("Name", "Age", "City")
                .addRow("Alice", "30", "New York")
                .addRow("Bob", "25", "Los Angeles")
                .addRow("Charlie", "35", "Chicago");
        a.print();

        IO.println(ConsoleColors.combine(
                ConsoleColors.YELLOW,
                ConsoleColors.BG_BLUE
        ) + "Hello, World!" + ConsoleColors.RESET);

        final var tonda = JXTriple.of("Tonda", 35, "Letnany");
        IO.println(tonda.getFirst());
        IO.println(tonda);

    }
}