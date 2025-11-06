package org.javaxtend.console;

import org.javaxtend.data.DataFrame;
import org.javaxtend.io.IO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleTableTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @TempDir
    Path tempDir;

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
    @DisplayName("Should print a simple table with correct formatting")
    void testPrintTable() {
        new ConsoleTable()
                .addRow("ID", "Name", "Role")
                .addRow("1", "John Doe", "Developer")
                .addRow("2", "Jane", "Manager")
                .print();

        String expected =
                """
                        +----+----------+-----------+
                        | ID | Name     | Role      |
                        +----+----------+-----------+
                        | 1  | John Doe | Developer |
                        | 2  | Jane     | Manager   |
                        +----+----------+-----------+
                        """;
        String actual = outContent.toString().replaceAll("\\r\\n", "\n");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should correctly calculate column width with ANSI color codes")
    void testPrintTableWithAnsiColors() {
        String coloredName = ConsoleColors.CYAN.colorize("Colored");
        new ConsoleTable()
                .addRow("Key", "Value")
                .addRow("Name", coloredName)
                .print();

        String actual = outContent.toString();
        String expectedBorder = "+------+---------+";
        assertEquals(expectedBorder, actual.split("\n")[0].trim());
    }

    @Test
    @DisplayName("Should correctly load and print data from a CSV file")
    void testFromCSV() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        List<String> lines = List.of(
                "Header1,Header2",
                "row1_col1,row1_col2",
                "row2_col1,row2_col2"
        );
        Files.write(csvFile, lines);

        new ConsoleTable()
                .fromCSV(csvFile.toString(), ",", true)
                .print();

        String expected =
                """
                        +-----------+-----------+
                        | Header1   | Header2   |
                        +-----------+-----------+
                        | row1_col1 | row1_col2 |
                        | row2_col1 | row2_col2 |
                        +-----------+-----------+
                        """;

        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    @DisplayName("fromDataFrame should create a ConsoleTable correctly")
    void testFromDataFrame() {
        Map<String, List<?>> data = new LinkedHashMap<>();
        data.put("Name", List.of("Test1", "Test2"));
        data.put("Value", List.of(10, 20));
        DataFrame df = DataFrame.of(data);

        ConsoleTable table = ConsoleTable.fromDataFrame(df);
        List<String[]> rows = table.getRows();

        assertEquals(3, rows.size(), "Should have header + 2 data rows");
        assertArrayEquals(new String[]{"Index", "Name", "Value"}, rows.get(0));
        assertArrayEquals(new String[]{"0", "Test1", "10"}, rows.get(1));
        assertArrayEquals(new String[]{"1", "Test2", "20"}, rows.get(2));
    }
}
