package org.javaxtend.console;

import org.javaxtend.io.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility for creating and printing formatted text-based tables in the console.
 * <p>
 * It supports adding rows dynamically and can automatically calculate column widths
 * to create a well-aligned table. It also provides a helper to load data from a CSV file.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>
 *     new ConsoleTable()
 *         .addRow("ID", "Name", "Role")
 *         .addRow("1", "John Doe", "Developer")
 *         .addRow("2", "Jane Smith", "Project Manager")
 *         .print();
 * </pre></blockquote>
 */
public class ConsoleTable {
    private final List<String[]> rows = new ArrayList<>();
    private final List<Integer> colWidths = new ArrayList<>();

    /**
     * Adds a new row to the table.
     * <p>
     * Column widths are automatically adjusted to fit the content of the new row.
     *
     * @param columns The string values for each cell in the row.
     * @return This {@code ConsoleTable} instance for method chaining.
     */
    public ConsoleTable addRow(String... columns) {
        rows.add(columns);

        for (int i = 0; i < columns.length; i++) {
            final int len = columns[i].replaceAll("\u001B\\[[;\\d]*m", "").length();
            if (colWidths.size() <= i) {
                colWidths.add(len);
            } else {
                colWidths.set(i, Math.max(colWidths.get(i), len));
            }
        }
        return this;
    }

    /**
     * Populates the table with data from a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @param delimiter The delimiter used in the CSV file (e.g., "," or ";").
     * @param hasHeader If true, the first line of the file is treated as the table header.
     * @return This {@code ConsoleTable} instance for method chaining.
     * @throws IOException If an I/O error occurs reading from the file.
     */
    public ConsoleTable fromCSV(String filePath, String delimiter, boolean hasHeader) throws IOException {
        try (final BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            if (hasHeader && (line = br.readLine()) != null) {
                addRow(line.split(delimiter, -1));
            }

            while ((line = br.readLine()) != null) {
                addRow(line.split(delimiter, -1));
            }
        }
        return this;
    }

    /**
     * Prints the formatted table to the console.
     */
    public void print() {
        if (rows.isEmpty()) return;
        final StringBuilder sb = new StringBuilder();
        final String border = buildBorder();
        sb.append(border).append("\n");
        for (int i = 0; i < rows.size(); i++) {
            final String[] row = rows.get(i);
            sb.append("|");
            for (int j = 0; j < colWidths.size(); j++) {
                final String cell = j < row.length ? row[j] : "";
                sb.append(" ").append(padRight(cell, colWidths.get(j))).append(" |");
            }
            sb.append("\n");
            if (i == 0 && rows.size() > 1) {
                sb.append(border).append("\n");
            }
        }

        sb.append(border);
        IO.println(sb);
    }

    private String buildBorder() {
        final StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < colWidths.size(); i++) {
            sb.append("-".repeat(colWidths.get(i) + 2)).append("+");
        }
        return sb.toString();
    }

    private String padRight(String text, int visibleLength) {
        final int ansiLength = text.length() - text.replaceAll("\u001B\\[[;\\d]*m", "").length();
        return String.format("%-" + (visibleLength + ansiLength) + "s", text);
    }
}
