package org.javaxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    @DisplayName("parse() should correctly parse a full version string")
    void parse_fullVersion() {
        Version v = Version.parse("1.2.3.4");
        assertEquals("1.2.3.4", v.toString());
    }

    @Test
    @DisplayName("parse() should handle shorter version strings")
    void parse_shortVersion() {
        Version v = Version.parse("1.2");
        assertEquals("1.2.0.0", v.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a.b.c", "1.a", "1.2.", "", " "})
    @DisplayName("parse() should throw exception for invalid formats")
    void parse_invalidFormat_throwsException(String invalidVersion) {
        assertThrows(IllegalArgumentException.class, () -> Version.parse(invalidVersion));
    }

    @Test
    @DisplayName("compareTo() should correctly compare versions")
    void compareTo_correctlyCompares() {
        Version v1_2_0 = Version.parse("1.2.0");
        Version v1_10_0 = Version.parse("1.10.0");
        Version v2_0_0 = Version.parse("2.0.0");
        Version v1_2_0_1 = Version.parse("1.2.0.1");

        assertTrue(v1_10_0.isGreaterThan(v1_2_0));
        assertTrue(v2_0_0.isGreaterThan(v1_10_0));
        assertTrue(v1_2_0_1.isGreaterThan(v1_2_0));
        assertTrue(v1_2_0.isLessThan(v1_2_0_1));
        assertEquals(0, v1_2_0.compareTo(Version.parse("1.2.0")));
    }

    @Test
    @DisplayName("equals() and hashCode() should work correctly")
    void equalsAndHashCode() {
        Version v1 = Version.parse("1.2.3");
        Version v2 = Version.parse("1.2.3.0");
        Version v3 = Version.parse("1.2.4");

        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1.hashCode(), v3.hashCode());
    }
}