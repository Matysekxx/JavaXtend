package org.javxtend.console;

import org.javxtend.io.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleTable {
    private final List<String[]> rows = new ArrayList<>();
    private final List<Integer> colWidths = new ArrayList<>();

    public ConsoleTable addRow(String... columns) {
        rows.add(columns);

        for (int i = 0; i < columns.length; i++) {
            int len = columns[i].length();
            if (colWidths.size() <= i) {
                colWidths.add(len);
            } else {
                colWidths.set(i, Math.max(colWidths.get(i), len));
            }
        }
        return this;
    }

    public ConsoleTable fromCSV(String filePath, String delimiter, boolean hasHeader) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(delimiter, -1);

                if (first && hasHeader) {
                    addRow(cols);
                    first = false;
                } else {
                    addRow(cols);
                }
            }
        }
        return this;
    }

    public void print() {
        if (rows.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        String border = buildBorder();

        sb.append(border).append("\n");

        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            sb.append("|");
            for (int j = 0; j < colWidths.size(); j++) {
                String cell = j < row.length ? row[j] : "";
                sb.append(" ").append(padRight(cell, colWidths.get(j))).append(" |");
            }
            sb.append("\n");
            if (i == 0) {
                sb.append(border).append("\n");
            }
        }

        sb.append(border);
        IO.println(sb);
    }

    private String buildBorder() {
        StringBuilder sb = new StringBuilder("+");
        for (Integer w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    private String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
}
