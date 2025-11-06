package org.javaxtend.data;

import org.javaxtend.console.ConsoleTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataFrameTest {

    @TempDir
    Path tempDir;

    private DataFrame df;
    private Index testIndex;

    @BeforeEach
    void setUp() {
        List<String> names = List.of("Alice", "Bob", "Charlie");
        List<Integer> ages = List.of(25, 30, 35);
        List<String> cities = List.of("Prague", "Brno", "Ostrava");
        List<String> rowLabels = List.of("id_1", "id_2", "id_3");

        testIndex = new Index(rowLabels);

        Map<String, Series<?>> columns = new LinkedHashMap<>();
        columns.put("name", new Series<>(names, rowLabels, "name"));
        columns.put("age", new Series<>(ages, rowLabels, "age"));
        columns.put("city", new Series<>(cities, rowLabels, "city"));

        df = new DataFrame(columns, testIndex, "TestDF");
    }

    @Test
    @DisplayName("Constructor should create DataFrame with valid data")
    void testConstructor_ValidData() {
        assertNotNull(df);
        assertEquals(3, df.size());
        assertEquals(List.of("name", "age", "city"), df.columns());
        assertEquals(List.of("id_1", "id_2", "id_3"), df.index());
    }

    @Test
    @DisplayName("Constructor should throw exception for mismatched sizes")
    void testConstructor_MismatchedSizes() {
        List<String> names = List.of("Alice", "Bob");
        List<String> rowLabels = List.of("id_1", "id_2", "id_3");

        Map<String, Series<?>> columns = Map.of("name", new Series<>(names, List.of("a", "b"), "name"));
        var index = new Index(rowLabels);

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            new DataFrame(columns, index, "MismatchedDF");
        });

        assertTrue(exception.getMessage().contains("has size 2, but expected 3"));
    }

    @Test
    @DisplayName("getByPosition should return correct row data")
    void testGetByPosition() {
        Map<String, Object> row = df.getByPosition(1);

        assertEquals(3, row.size());
        assertEquals("Bob", row.get("name"));
        assertEquals(30, row.get("age"));
        assertEquals("Brno", row.get("city"));
    }

    @Test
    @DisplayName("getByPosition should throw for out-of-bounds index")
    void testGetByPosition_OutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> df.getByPosition(3));
        assertThrows(IllegalArgumentException.class, () -> df.getByPosition(-1));
    }

    @Test
    @DisplayName("getByLabel should return correct row data")
    void testGetByLabel() {
        Map<String, Object> row = df.getByLabel("id_3");

        assertEquals(3, row.size());
        assertEquals("Charlie", row.get("name"));
        assertEquals(35, row.get("age"));
        assertEquals("Ostrava", row.get("city"));
    }

    @Test
    @DisplayName("getByLabel should throw KeyException for non-existent label")
    void testGetByLabel_NotFound() {
        assertThrows(KeyException.class, () -> df.getByLabel("non_existent_id"));
    }

    @Test
    @DisplayName("slice should return a correct subset of the DataFrame")
    void testSlice() {
        DataFrame sliced = df.slice(1, 3);

        assertEquals(2, sliced.size());
        assertEquals(List.of("id_2", "id_3"), sliced.index());

        Map<String, Object> firstRow = sliced.getByPosition(0);
        assertEquals("Bob", firstRow.get("name"));
        assertEquals(30, firstRow.get("age"));

        Map<String, Object> lastRow = sliced.getByPosition(1);
        assertEquals("Charlie", lastRow.get("name"));
        assertEquals(35, lastRow.get("age"));
    }

    @Test
    @DisplayName("slice with invalid range should throw exception")
    void testSlice_InvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> df.slice(0, 4));
        assertThrows(IllegalArgumentException.class, () -> df.slice(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> df.slice(2, 1));
    }

    @Test
    @DisplayName("head should return the first n rows")
    void testHead() {
        DataFrame headDf = df.head(2);

        assertEquals(2, headDf.size());
        assertEquals(List.of("id_1", "id_2"), headDf.index());
        assertEquals("Alice", headDf.getByPosition(0).get("name"));
        assertEquals("Bob", headDf.getByPosition(1).get("name"));
    }

    @Test
    @DisplayName("head with n > size should return all rows")
    void testHead_MoreThanSize() {
        DataFrame headDf = df.head(10);
        assertEquals(3, headDf.size());
        assertEquals(df.index(), headDf.index());
    }

    @Test
    @DisplayName("head with n <= 0 should return an empty DataFrame")
    void testHead_ZeroOrNegative() {
        DataFrame headZero = df.head(0);
        assertEquals(0, headZero.size());

        DataFrame headNegative = df.head(-5);
        assertEquals(0, headNegative.size());
    }

    @Test
    @DisplayName("of() factory should create a DataFrame from a map of lists")
    void testOf() {
        Map<String, List<?>> data = new LinkedHashMap<>();
        data.put("product", List.of("A", "B"));
        data.put("sales", List.of(100, 200));

        DataFrame newDf = DataFrame.of(data);

        assertEquals(2, newDf.size());
        assertEquals(List.of("product", "sales"), newDf.columns());
        assertEquals(100, newDf.get("sales").getByPosition(0));
        assertEquals(List.of(0, 1), newDf.index());
    }

    @Test
    @DisplayName("of() factory should throw for lists of different sizes")
    void testOf_MismatchedSizes() {
        Map<String, List<?>> data = Map.of(
                "product", List.of("A", "B"),
                "sales", List.of(100)
        );
        assertThrows(IllegalArgumentException.class, () -> DataFrame.of(data));
    }

    @Test
    @DisplayName("withColumn should add a new column")
    void testWithColumn_AddNew() {
        Series<Double> ratings = new Series<>(List.of(4.5, 5.0, 4.0), df.index().stream().toList(), "ratings");
        DataFrame newDf = df.withColumn("ratings", ratings);

        assertEquals(4, newDf.columns().size());
        assertTrue(newDf.columns().contains("ratings"));
        assertEquals(5.0, newDf.get("ratings").getByLabel("id_2"));
    }

    @Test
    @DisplayName("withColumn should replace an existing column")
    void testWithColumn_ReplaceExisting() {
        Series<Integer> newAges = new Series<>(List.of(26, 31, 36), df.index().stream().toList(), "age");
        DataFrame newDf = df.withColumn("age", newAges);

        assertEquals(3, newDf.columns().size());
        assertEquals(31, newDf.get("age").getByLabel("id_2"));
    }

    @Test
    @DisplayName("withColumn should throw for series with different size")
    void testWithColumn_MismatchedSize() {
        Series<Double> ratings = new Series<>(List.of(4.5, 5.0), List.of("a", "b"), "ratings");
        assertThrows(IllegalArgumentException.class, () -> df.withColumn("ratings", ratings));
    }

    @Test
    @DisplayName("dropColumn should remove a column")
    void testDropColumn() {
        DataFrame newDf = df.dropColumn("city");
        assertEquals(2, newDf.columns().size());
        assertFalse(newDf.columns().contains("city"));
    }

    @Test
    @DisplayName("dropColumn should throw for non-existent column")
    void testDropColumn_NotFound() {
        assertThrows(KeyException.class, () -> df.dropColumn("non_existent_column"));
    }

    @Test
    @DisplayName("filter should return rows matching the predicate")
    void testFilter() {
        // The old, less safe way
        DataFrame filteredDf1 = df.filter(row -> row.getInt("age") > 30);
        assertEquals(1, filteredDf1.size());
        assertEquals("Charlie", filteredDf1.getByLabel("id_3").get("name"));
    }

    @Test
    @DisplayName("filter with type-safe Row should return correct rows")
    void testFilter_TypeSafe() {
        // The new, type-safe way using Row
        DataFrame filteredDf = df.filter(row -> row.getInt("age") > 30);

        assertEquals(1, filteredDf.size());
        assertEquals("Charlie", filteredDf.getByLabel("id_3").get("name"));
    }

    @Test
    @DisplayName("filter should return an empty DataFrame if no rows match")
    void testFilter_NoMatches() {
        DataFrame filteredDf = df.filter(row -> row.getInt("age") > 100);
        assertEquals(0, filteredDf.size());
    }

    @Test
    @DisplayName("toCSV and fromCSV should correctly save and load a DataFrame")
    void testToCSV_and_fromCSV() throws IOException {
        Path csvPath = tempDir.resolve("test.csv");
        String filePath = csvPath.toString();

        df.toCSV(filePath);

        DataFrame loadedDf = DataFrame.fromCSV(filePath);

        assertEquals(df.size(), loadedDf.size());
        assertEquals(df.columns(), loadedDf.columns());

        assertEquals(String.valueOf(df.get("age").getByPosition(0)), loadedDf.get("age").getByPosition(0));
        assertEquals(df.get("name").getByPosition(1), loadedDf.get("name").getByPosition(1));
        assertEquals(df.get("city").getByPosition(2), loadedDf.get("city").getByPosition(2));
        assertEquals(List.of(0, 1, 2), loadedDf.index());
    }


    @Test
    @DisplayName("fromConsoleTable should create a DataFrame correctly")
    void testFromConsoleTable() {
        ConsoleTable table = new ConsoleTable();
        table.addRow("Index", "Name", "Score");
        table.addRow("P1", "Alpha", "100");
        table.addRow("P2", "Beta", "200");

        DataFrame convertedDf = DataFrame.fromConsoleTable(table);

        assertEquals(2, convertedDf.size());
        assertEquals(List.of("Name", "Score"), convertedDf.columns());
        assertEquals(List.of("P1", "P2"), convertedDf.index());
        assertEquals("Beta", convertedDf.get("Name").getByLabel("P2"));
        assertEquals("100", convertedDf.get("Score").getByLabel("P1"));
    }

    @Test
    @DisplayName("groupBy and sum should correctly aggregate data")
    void testGroupBy_Sum() {
        DataFrame salesDf = DataFrame.of(Map.of(
                "region", List.of("East", "West", "East", "West"),
                "sales", List.of(100, 150, 200, 120)
        ));

        DataFrame result = salesDf.groupBy("region").sum("sales");

        assertEquals(2, result.size());
        assertEquals(300.0, result.get("sales_sum").getByLabel("East"));
        assertEquals(270.0, result.get("sales_sum").getByLabel("West"));
    }

    @Test
    @DisplayName("groupBy and mean should correctly aggregate data")
    void testGroupBy_Mean() {
        DataFrame salesDf = DataFrame.of(Map.of(
                "region", List.of("East", "West", "East", "West"),
                "sales", List.of(100, 150, 200, 120)
        ));

        DataFrame result = salesDf.groupBy("region").mean("sales");

        assertEquals(2, result.size());
        assertEquals(150.0, result.get("sales_mean").getByLabel("East"));
        assertEquals(135.0, result.get("sales_mean").getByLabel("West"));
    }
}