package org.javaxtend.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
        trie.insert("car");
        trie.insert("card");
        trie.insert("carpet");
        trie.insert("java");
        trie.insert("jar");
    }

    @Test
    @DisplayName("search() should find existing words")
    void search_findsExistingWords() {
        assertTrue(trie.search("car"));
        assertTrue(trie.search("carpet"));
        assertTrue(trie.search("java"));
    }

    @Test
    @DisplayName("search() should not find non-existent words or prefixes")
    void search_doesNotFindNonExistentWords() {
        assertFalse(trie.search("carp"), "Should not find a prefix that is not a full word");
        assertFalse(trie.search("jav"), "Should not find a prefix that is not a full word");
        assertFalse(trie.search("python"), "Should not find a word that was never inserted");
    }

    @Test
    @DisplayName("startsWith() should find existing prefixes")
    void startsWith_findsExistingPrefixes() {
        assertTrue(trie.startsWith("c"));
        assertTrue(trie.startsWith("car"));
        assertTrue(trie.startsWith("carp"), "Should find prefix of 'carpet'");
        assertTrue(trie.startsWith("j"));
        assertTrue(trie.startsWith("java"), "Should find prefix for a full word");
    }

    @Test
    @DisplayName("startsWith() should not find non-existent prefixes")
    void startsWith_doesNotFindNonExistentPrefixes() {
        assertFalse(trie.startsWith("p"));
        assertFalse(trie.startsWith("cart"));
    }

    @Test
    @DisplayName("findWordsWithPrefix() should return all matching words")
    void findWordsWithPrefix_returnsAllMatchingWords() {
        List<String> suggestions = trie.findWordsWithPrefix("car");
        assertEquals(3, suggestions.size());
        assertTrue(suggestions.containsAll(List.of("car", "card", "carpet")));
    }

    @Test
    @DisplayName("findWordsWithPrefix() should handle a prefix that is also a word")
    void findWordsWithPrefix_forPrefixThatIsAWord() {
        List<String> suggestions = trie.findWordsWithPrefix("car");
        assertTrue(suggestions.contains("car"), "Result should include the prefix itself if it's a word");
    }

    @Test
    @DisplayName("findWordsWithPrefix() should return an empty list for a non-existent prefix")
    void findWordsWithPrefix_forNonExistentPrefix_returnsEmptyList() {
        List<String> suggestions = trie.findWordsWithPrefix("xyz");
        assertTrue(suggestions.isEmpty());
    }

    @Test
    @DisplayName("findWordsWithPrefix() with empty string should return all words")
    void findWordsWithPrefix_withEmptyString_returnsAllWords() {
        List<String> suggestions = trie.findWordsWithPrefix("");
        assertEquals(5, suggestions.size());
        assertTrue(suggestions.containsAll(List.of("car", "card", "carpet", "java", "jar")));
    }

    @Test
    @DisplayName("insert() should handle inserting the same word multiple times")
    void insert_handlesDuplicates() {
        // Trie should contain 5 unique words initially
        assertEquals(5, trie.findWordsWithPrefix("").size());

        // Insert an existing word
        trie.insert("car");

        // The number of unique words should not change
        assertEquals(5, trie.findWordsWithPrefix("").size());
        assertTrue(trie.search("car"));
    }

    @Test
    @DisplayName("insert() and search() should handle empty string")
    void insertAndSearch_handlesEmptyString() {
        Trie localTrie = new Trie();
        assertFalse(localTrie.search(""), "Empty trie should not contain empty string");

        localTrie.insert("");
        assertTrue(localTrie.search(""), "Should find empty string after insertion");

        localTrie.insert("a");
        assertTrue(localTrie.search(""), "Should still find empty string");
        assertTrue(localTrie.search("a"));
    }
}