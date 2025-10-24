package org.javaxtend.validation;

/**
 * A utility class providing a single entry point for common validation checks (guard clauses).
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * public void process(String name, int amount) {
 *     Guard.against().nullOrBlank(name, "name");
 *     Guard.against().negativeOrZero(amount, "amount");
 *     // ...
 * }
 * }</pre></blockquote>
 */
public final class Guard {
    private static final GuardClause INSTANCE = new GuardClause();

    private Guard() {}

    public static GuardClause against() { return INSTANCE; }
}