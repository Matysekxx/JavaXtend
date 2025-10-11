package org.javaxtend.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndexTest {

    @Test
    @DisplayName("Constructor should create an immutable index")
    void constructor_createsImmutableIndex() {
        var originalLabels = new ArrayList<>(List.of("a", "b"));
        var index = new Index(originalLabels);

        originalLabels.add("c");

        assertEquals(2, index.size(), "Index size should not change");
        assertNotEquals(originalLabels, index.toList(), "Index labels should be an immutable copy");
    }

    @Test
    @DisplayName("size() should return the correct number of labels")
    void size_returnsCorrectCount() {
        var index = new Index(List.of("a", "b", "c"));
        assertEquals(3, index.size());
    }

    @Test
    @DisplayName("getPosition() should return correct position for a label")
    void getPosition_withValidLabel_returnsPosition() {
        var index = new Index(List.of("a", "b", "c"));
        assertEquals(1, index.getPosition("b"));
    }

    @Test
    @DisplayName("getPosition() should throw KeyException for a non-existent label")
    void getPosition_withInvalidLabel_throwsKeyException() {
        var index = new Index(List.of("a", "b", "c"));
        assertThrows(KeyException.class, () -> index.getPosition("d"));
    }

    @Test
    @DisplayName("getPosition() should return the last position for duplicate labels")
    void getPosition_withDuplicateLabels_returnsLastPosition() {
        var index = new Index(List.of("a", "b", "a"));
        assertEquals(2, index.getPosition("a"));
    }

    @Test
    @DisplayName("toList() should return an immutable list of labels")
    void toList_returnsImmutableList() {
        var index = new Index(List.of("a", "b"));
        var labels = index.toList();
        assertThrows(UnsupportedOperationException.class, () -> labels.add("c"));
    }

    @Test
    @DisplayName("equals() and hashCode() should be based on labels")
    void equalsAndHashCode_areBasedOnLabels() {
        var index1 = new Index(List.of("a", "b"));
        var index2 = new Index(List.of("a", "b"));
        var index3 = new Index(List.of("a", "c"));

        assertEquals(index1, index2);
        assertNotEquals(index1, index3);
        assertEquals(index1.hashCode(), index2.hashCode());
        assertNotEquals(index1.hashCode(), index3.hashCode());
    }
}