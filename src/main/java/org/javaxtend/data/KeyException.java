package org.javaxtend.data;

/**
 * An unchecked exception thrown when a key is not found in an index.
 */
public class KeyException extends RuntimeException {
    public KeyException(String message) {
        super(message);
    }
}