package org.javaxtend.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Trie (prefix tree) data structure for efficient storage and retrieval of strings.
 * <p>
 * It is optimized for prefix-based searches, making it ideal for applications
 * like autocomplete or spell-checking.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * Trie dictionary = new Trie();
 * dictionary.insert("car");
 * dictionary.insert("card");
 * dictionary.insert("carpet");
 *
 * boolean hasCarpet = dictionary.search("carpet"); // true
 * boolean hasPrefixCar = dictionary.startsWith("car"); // true
 * List<String> suggestions = dictionary.findWordsWithPrefix("car"); // ["car", "card", "carpet"]
 * }</pre></blockquote>
 */
public class Trie {

    private static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean isEndOfWord = false;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts a word into the trie.
     * @param word The word to insert.
     */
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    /**
     * Searches for a complete word in the trie.
     * @param word The word to search for.
     * @return {@code true} if the word exists in the trie, {@code false} otherwise.
     */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Checks if there is any word in the trie that starts with the given prefix.
     * @param prefix The prefix to check.
     * @return {@code true} if a word with the prefix exists, {@code false} otherwise.
     */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /**
     * Finds all words in the trie that start with the given prefix.
     * @param prefix The prefix to search for.
     * @return A list of all words starting with the prefix.
     */
    public List<String> findWordsWithPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode startNode = findNode(prefix);
        if (startNode != null) {
            findAllWords(startNode, new StringBuilder(prefix), results);
        }
        return results;
    }

    private TrieNode findNode(String str) {
        TrieNode current = root;
        for (char c : str.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private void findAllWords(TrieNode node, StringBuilder currentPrefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(currentPrefix.toString());
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            currentPrefix.append(entry.getKey());
            findAllWords(entry.getValue(), currentPrefix, results);
            currentPrefix.deleteCharAt(currentPrefix.length() - 1);
        }
    }
}