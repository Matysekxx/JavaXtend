package org.javaxtend.util;

import org.javaxtend.functional.Maybe;

import java.util.Collection;

/**
 * A utility for finding the best approximate match for a string from a collection of options.
 * <p>
 * It uses the Levenshtein distance algorithm to calculate the "edit distance" between strings.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * String userInput = "comit";
 * List<String> availableCommands = List.of("commit", "push", "pull");
 *
 * Maybe<String> suggestion = FuzzyStringMatcher.findBestMatch(userInput, availableCommands);
 *
 * suggestion.ifJust(command ->
 *     System.out.println("Did you mean '" + command + "'?") // Prints: Did you mean 'commit'?
 * );
 * }</pre></blockquote>
 */
public final class FuzzyStringMatcher {

    private FuzzyStringMatcher() {}

    /**
     * Finds the best match for a given input string from a collection of options.
     *
     * @param input   The string to find a match for.
     * @param options A collection of potential matches.
     * @return A {@code Maybe.Just} containing the best match, or {@code Maybe.Nothing} if no suitable match is found.
     */
    public static Maybe<String> findBestMatch(String input, Collection<String> options) {
        if (input == null || input.isBlank() || options == null || options.isEmpty()) {
            return Maybe.nothing();
        }
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;
        for (String option : options) {
            final int distance = calculateLevenshteinDistance(input, option);
            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = option;
            }
        }
        return minDistance < input.length() / 2 ? Maybe.just(bestMatch) : Maybe.nothing();
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return The Levenshtein distance.
     */
    private static int calculateLevenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        final int m = s1.length();
        final int n = s2.length();
        final int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            final char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                final char c2 = s2.charAt(j - 1);
                final int cost = (c1 == c2) ? 0 : 1;

                final int deletion = dp[i - 1][j] + 1;
                final int insertion = dp[i][j - 1] + 1;
                final int substitution = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(Math.min(deletion, insertion), substitution);
            }
        }

        return dp[m][n];
    }
}