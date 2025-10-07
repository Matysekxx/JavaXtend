package org.javaxtend.util;

import org.javaxtend.functional.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JXCollectionsTest {

    private record User(int id, String name, int score) {}

    @Test
    void headOption_returnsJustForNonEmptyList() {
        List<String> list = List.of("a", "b", "c");
        Maybe<String> head = JXCollections.headOption(list);
        assertTrue(head.isJust());
        assertEquals("a", head.unwrap());
    }

    @Test
    void headOption_returnsJustForNonEmptySet() {
        Set<Integer> set = new LinkedHashSet<>(List.of(1, 2, 3));
        Maybe<Integer> head = JXCollections.headOption(set);
        assertTrue(head.isJust());
        assertEquals(1, head.unwrap());
    }

    @Test
    void headOption_returnsNothingForEmptyList() {
        List<String> list = new ArrayList<>();
        Maybe<String> head = JXCollections.headOption(list);
        assertTrue(head.isNothing());
    }

    @Test
    void headOption_returnsNothingForNullCollection() {
        Maybe<String> head = JXCollections.headOption(null);
        assertTrue(head.isNothing());
    }

    @Test
    void lastOption_returnsJustForNonEmptyList() {
        List<String> list = List.of("a", "b", "c");
        Maybe<String> last = JXCollections.lastOption(list);
        assertTrue(last.isJust());
        assertEquals("c", last.unwrap());
    }

    @Test
    void lastOption_returnsNothingForEmptyList() {
        List<String> list = List.of();
        assertTrue(JXCollections.lastOption(list).isNothing());
    }

    @Test
    void getOption_returnsJustForValidIndex() {
        List<String> list = List.of("a", "b", "c");
        Maybe<String> item = JXCollections.getOption(list, 1);
        assertTrue(item.isJust());
        assertEquals("b", item.unwrap());
    }

    @Test
    void getOption_returnsNothingForInvalidIndex() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(JXCollections.getOption(list, -1).isNothing());
        assertTrue(JXCollections.getOption(list, 3).isNothing());
    }

    @Test
    void find_returnsFirstMatchingElement() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Maybe<Integer> firstEven = JXCollections.find(numbers, n -> n % 2 == 0);
        assertTrue(firstEven.isJust());
        assertEquals(2, firstEven.unwrap());
    }

    @Test
    void find_returnsNothingWhenNoElementMatches() {
        List<Integer> numbers = List.of(1, 3, 5);
        Maybe<Integer> firstEven = JXCollections.find(numbers, n -> n % 2 == 0);
        assertTrue(firstEven.isNothing());
    }

    @Test
    void findAll_returnsMatchingElements() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<Integer> evens = JXCollections.findAll(numbers, n -> n % 2 == 0);
        assertEquals(List.of(2, 4), evens);
    }

    @Test
    void findAll_returnsEmptyListWhenNoMatches() {
        List<Integer> numbers = List.of(1, 3, 5);
        List<Integer> evens = JXCollections.findAll(numbers, n -> n % 2 == 0);
        assertTrue(evens.isEmpty());
    }

    @Test
    void zip_combinesTwoLists() {
        List<String> names = List.of("Alice", "Bob");
        List<Integer> ages = List.of(30, 25, 40);
        List<ImmutablePair<String, Integer>> pairs = JXCollections.zip(names, ages);

        assertEquals(2, pairs.size());
        assertEquals(ImmutablePair.of("Alice", 30), pairs.get(0));
        assertEquals(ImmutablePair.of("Bob", 25), pairs.get(1));
    }

    @Test
    void partition_splitsCollectionCorrectly() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        ImmutablePair<List<Integer>, List<Integer>> parts = JXCollections.partition(numbers, n -> n % 2 == 0);
        
        assertEquals(List.of(2, 4, 6), parts.getFirst());
        assertEquals(List.of(1, 3, 5), parts.getSecond());
    }

    @Test
    void distinctBy_returnsDistinctElementsBasedOnKey() {
        User user1 = new User(1, "Alice", 100);
        User user2 = new User(2, "Bob", 150);
        User user3 = new User(1, "Alicia", 120);

        List<User> users = List.of(user1, user2, user3);
        List<User> distinctUsers = JXCollections.distinctBy(users, User::id);

        assertEquals(2, distinctUsers.size());
        assertEquals(user1, distinctUsers.get(0));
        assertEquals(user2, distinctUsers.get(1));
    }

    @Test
    void chunked_splitsListIntoEvenChunks() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        List<List<Integer>> chunks = JXCollections.chunked(numbers, 2);

        assertEquals(3, chunks.size());
        assertEquals(List.of(1, 2), chunks.get(0));
        assertEquals(List.of(3, 4), chunks.get(1));
        assertEquals(List.of(5, 6), chunks.get(2));
    }

    @Test
    void chunked_handlesLastChunkBeingSmaller() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<List<Integer>> chunks = JXCollections.chunked(numbers, 2);

        assertEquals(3, chunks.size());
        assertEquals(List.of(1, 2), chunks.get(0));
        assertEquals(List.of(3, 4), chunks.get(1));
        assertEquals(List.of(5), chunks.get(2));
    }

    @Test
    void sortBy_withKeyExtractor_sortsCorrectly() {
        User user1 = new User(1, "Bob", 150);
        User user2 = new User(2, "Alice", 100);
        User user3 = new User(3, "Charlie", 200);

        List<User> users = List.of(user1, user2, user3);
        List<User> sortedByScore = JXCollections.sortBy(users, User::score);

        assertEquals(List.of(user2, user1, user3), sortedByScore);
    }

    @Test
    void sortBy_withComparator_sortsCorrectly() {
        User user1 = new User(1, "Charlie", 150);
        User user2 = new User(2, "Alice", 100);
        User user3 = new User(3, "Bob", 200);

        List<User> users = List.of(user1, user2, user3);
        List<User> sortedByName = JXCollections.sortBy(users, Comparator.comparing(User::name));

        assertEquals(List.of(user2, user3, user1), sortedByName);
    }

    @Test
    void sortBy_withNaturalOrder_sortsCorrectly() {
        List<String> names = List.of("Charlie", "Alice", "Bob");
        List<String> sortedNames = JXCollections.sortBy(names);

        assertEquals(List.of("Alice", "Bob", "Charlie"), sortedNames);
    }

    @Test
    void minBy_withKeyExtractor_findsMinimum() {
        User user1 = new User(1, "Bob", 150);
        User user2 = new User(2, "Alice", 100);
        Maybe<User> youngest = JXCollections.minBy(List.of(user1, user2), User::score);
        assertEquals(user2, youngest.unwrap());
    }

    @Test
    void minBy_withComparator_findsMinimum() {
        User user1 = new User(1, "Christopher", 150);
        User user2 = new User(2, "Al", 100);
        Maybe<User> shortestName = JXCollections.minBy(List.of(user1, user2), Comparator.comparing(u -> u.name().length()));
        assertEquals(user2, shortestName.unwrap());
    }

    @Test
    void maxBy_withComparator_findsMaximum() {
        User user1 = new User(1, "Bob", 150);
        User user2 = new User(2, "Alice", 100);
        Maybe<User> longestName = JXCollections.maxBy(List.of(user1, user2), Comparator.comparing(u -> u.name().length()));
        assertEquals(user2, longestName.unwrap());
    }
    
    @Test
    void maxBy_withKeyExtractor_findsMaximum() {
        User user1 = new User(1, "Bob", 150);
        User user2 = new User(2, "Alice", 200);
        Maybe<User> highestScore = JXCollections.maxBy(List.of(user1, user2), User::score);
        assertEquals(user2, highestScore.unwrap());
    }

    @Test
    void minBy_returnsNothingForEmptyList() {
        List<User> emptyList = List.of();
        assertTrue(JXCollections.minBy(emptyList, User::score).isNothing());
        assertTrue(JXCollections.maxBy(emptyList, User::score).isNothing());
    }

    @Test
    void groupBy_groupsElementsByKey() {
        User alice = new User(1, "Alice", 100);
        User bob = new User(2, "Bob", 150);
        User charlie = new User(3, "Charlie", 100);

        List<User> users = List.of(alice, bob, charlie);
        Map<Integer, List<User>> usersByScore = JXCollections.groupBy(users, User::score);

        assertEquals(2, usersByScore.size());
        assertEquals(List.of(alice, charlie), usersByScore.get(100));
        assertEquals(List.of(bob), usersByScore.get(150));
    }

    @Test
    void groupBy_returnsEmptyMapForEmptyCollection() {
        List<User> emptyList = List.of();
        Map<Integer, List<User>> usersByScore = JXCollections.groupBy(emptyList, User::score);

        assertTrue(usersByScore.isEmpty());
    }
}