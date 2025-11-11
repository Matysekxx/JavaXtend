package org.javaxtend.util;

import org.javaxtend.validation.Guard;

import java.util.Objects;

/**
 * Represents a version number, inspired by C#'s System.Version.
 * <p>
 * It allows for parsing and comparing version strings (e.g., "1.2.3").
 * This class is immutable and thread-safe.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * Version v1 = Version.parse("1.2.0");
 * Version v2 = Version.parse("1.10.0");
 *
 * if (v2.isGreaterThan(v1)) {
 *     System.out.println("Version 2 is newer.");
 * }
 * }</pre></blockquote>
 */
public final class Version implements Comparable<Version> {

    private final int major;
    private final int minor;
    private final int build;
    private final int revision;

    private Version(int major, int minor, int build, int revision) {
        Guard.against().negative(major, "major");
        Guard.against().negative(minor, "minor");
        Guard.against().negative(build, "build");
        Guard.against().negative(revision, "revision");

        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    /**
     * Parses a version string into a Version object.
     * <p>
     * Handles formats like "1.2", "1.2.3", or "1.2.3.4".
     *
     * @param versionString The string to parse.
     * @return A new Version instance.
     * @throws IllegalArgumentException if the version string is null, blank, or has an invalid format.
     */
    public static Version parse(String versionString) {
        Guard.against().nullOrBlank(versionString, "versionString");

        String[] parts = versionString.split("\\.", -1);
        int major = 0, minor = 0, build = 0, revision = 0;

        try {
            for (String part : parts) {
                if (part.isEmpty()) {
                    throw new IllegalArgumentException("Version string cannot contain empty parts.");
                }
            }
            if (parts.length > 0) major = Integer.parseInt(parts[0]);
            if (parts.length > 1) minor = Integer.parseInt(parts[1]);
            if (parts.length > 2) build = Integer.parseInt(parts[2]);
            if (parts.length > 3) revision = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Version string contains non-numeric parts.", e);
        }

        return new Version(major, minor, build, revision);
    }


    public boolean isGreaterThan(Version other) {
        return this.compareTo(other) > 0;
    }

    public boolean isLessThan(Version other) {
        return this.compareTo(other) < 0;
    }

    @Override
    public int compareTo(Version other) {
        Objects.requireNonNull(other, "other version cannot be null");
        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        if (this.build != other.build) {
            return Integer.compare(this.build, other.build);
        }
        if (this.revision != other.revision) {
            return Integer.compare(this.revision, other.revision);
        }
        return 0;
    }


    @Override
    public String toString() {
        return major + "." +
                minor + "." +
                build + "." +
                revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && build == version.build && revision == version.revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, build, revision);
    }
}