package org.javxtend;

import org.javxtend.console.ConsoleColors;
import org.javxtend.console.ConsoleForm;
import org.javxtend.console.ConsoleTable;
import org.javxtend.util.JXTriple;

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

        ConsoleForm ab = new ConsoleForm()
                .addStringField("name", "jak se jmenujes")
                .addIntegerField("age", "kolik ti je let", 0, 99)
                .addYesNoField("gayness", "jsi teplej", true);

        var abs = ab.display();
        IO.println(abs);



    }
}