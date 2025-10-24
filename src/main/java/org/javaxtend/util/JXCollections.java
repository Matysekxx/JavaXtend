package org.javaxtend.util;

import org.javaxtend.functional.Maybe;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A utility class providing safe and functional-style operations for Java Collections.
 */
public final class JXCollections {

    private JXCollections() {}

    /**
     * Safely returns the first element of a collection.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * JXCollections.headOption(List.of("a", "b")); // Maybe.just("a")
     * JXCollections.headOption(List.of());      // Maybe.nothing()
     * }</pre></blockquote>
     *
     * @param collection The collection.
     * @param <T> The type of elements in the collection.
     * @return A {@code Maybe.Just} containing the first element, or {@code Maybe.Nothing} if the collection is null or empty.
     */
    public static <T> Maybe<T> headOption(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Maybe.nothing();
        }
        return Maybe.just(collection.iterator().next());
    }

    /**
     * Safely returns the last element of a collection.
     * <p>
     * Note: For collections without a defined order, the result is arbitrary.
     * This operation is efficient for {@link List} but requires a full iteration for other collection types.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * JXCollections.lastOption(List.of("a", "b", "c")); // Maybe.just("c")
     * JXCollections.lastOption(List.of());           // Maybe.nothing()
     * }</pre></blockquote>
     *
     * @param collection The collection.
     * @param <T> The type of elements in the collection.
     * @return A {@code Maybe.Just} containing the last element, or {@code Maybe.Nothing} if the collection is null or empty.
     */
    public static <T> Maybe<T> lastOption(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Maybe.nothing();
        }

        if (collection instanceof List<T> list) {
            return Maybe.just(list.getLast());
        }

        T lastElement = null;
        for (T element : collection) {
            lastElement = element;
        }
        return Maybe.ofNullable(lastElement);
    }

    /**
     * Safely returns the element at the specified index in a list.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * JXCollections.getOption(List.of("a", "b", "c"), 1);  // Maybe.just("b")
     * JXCollections.getOption(List.of("a", "b", "c"), 99); // Maybe.nothing()
     * }</pre></blockquote>
     *
     * @param list The list.
     * @param index The index of the element to return.
     * @param <T> The type of elements in the list.
     * @return A {@code Maybe.Just} containing the element, or {@code Maybe.Nothing} if the list is null or the index is out of bounds.
     */
    public static <T> Maybe<T> getOption(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return Maybe.nothing();
        }
        return Maybe.just(list.get(index));
    }

    /**
     * Finds the first element in the collection that satisfies a predicate.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * JXCollections.find(List.of(1, 2, 3, 4), n -> n % 2 == 0); // Maybe.just(2)
     * }</pre></blockquote>
     *
     * @param collection The collection to search in.
     * @param predicate The condition to test elements with.
     * @param <T> The type of elements in the collection.
     * @return A {@code Maybe.Just} containing the first matching element, or {@code Maybe.Nothing} if no element matches or the collection is null.
     */
    public static <T> Maybe<T> find(
            Collection<T> collection,
            Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        if (collection == null) {
            return Maybe.nothing();
        }
        for (T element : collection) {
            if (predicate.test(element)) {
                return Maybe.just(element);
            }
        }
        return Maybe.nothing();
    }

    /**
     * Returns a list containing all elements from the collection that satisfy a predicate.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var numbers = List.of(1, 2, 3, 4, 5);
     * JXCollections.findAll(numbers, n -> n % 2 == 0); // Returns: [2, 4]
     * }</pre></blockquote>
     *
     * @param collection The collection to search in.
     * @param predicate The condition to test elements with.
     * @param <T> The type of elements in the collection.
     * @return A new list containing all matching elements.
     */
    public static <T> List<T> findAll(
            Collection<T> collection,
            Predicate<T> predicate)
    {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        if (collection == null) {
            return List.of();
        }
        return collection.stream().filter(predicate).toList();
    }

    /**
     * Combines two collections into a list of pairs.
     * The resulting list will have the length of the shorter of the two input collections.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var names = List.of("Alice", "Bob");
     * var ages = List.of(30, 25, 40);
     * JXCollections.zip(names, ages); // Returns: [("Alice", 30), ("Bob", 25)]
     * }</pre></blockquote>
     *
     * @param collectionA The first collection.
     * @param collectionB The second collection.
     * @param <A> The element type of the first collection.
     * @param <B> The element type of the second collection.
     * @return A new {@code List} of {@code ImmutablePair}s.
     */
    public static <A, B> List<ImmutablePair<A, B>> zip(
            Collection<A> collectionA,
            Collection<B> collectionB
    ) {
        if (collectionA == null || collectionB == null || collectionA.isEmpty() || collectionB.isEmpty()) {
            return List.of();
        }

        Iterator<A> iteratorA = collectionA.iterator();
        Iterator<B> iteratorB = collectionB.iterator();

        final int size = Math.min(collectionA.size(), collectionB.size());
        final List<ImmutablePair<A, B>> pairs = new ArrayList<>(size);

        while (iteratorA.hasNext() && iteratorB.hasNext()) {
            pairs.add(ImmutablePair.of(iteratorA.next(), iteratorB.next()));
        }

        return pairs;
    }

    /**
     * Splits the original collection into a pair of lists,
     * where the first list contains elements for which the predicate returned {@code true},
     * and the second list contains elements for which it returned {@code false}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var numbers = List.of(1, 2, 3, 4, 5);
     * var parts = JXCollections.partition(numbers, n -> n % 2 == 0);
     * // parts.first() is [2, 4], parts.second() is [1, 3, 5]
     * }</pre></blockquote>
     *
     * @param collection The collection to partition.
     * @param predicate  The predicate to apply to each element.
     * @param <T>        The type of elements in the collection.
     * @return An {@code ImmutablePair} containing two lists.
     */
    public static <T> ImmutablePair<List<T>, List<T>> partition(
            Collection<T> collection,
            Predicate<T> predicate
    ) {
        if (collection == null) {
            return ImmutablePair.of(List.of(), List.of());
        }
        Objects.requireNonNull(predicate, "predicate cannot be null");

        final List<T> matching = new ArrayList<>();
        final List<T> nonMatching = new ArrayList<>();
        for (T element : collection) {
            if (predicate.test(element)) {
                matching.add(element);
            } else {
                nonMatching.add(element);
            }
        }
        return ImmutablePair.of(
                matching, nonMatching
        );
    }

    /**
     * Returns a list containing only elements from the given collection
     * that have a distinct key returned by the given {@code keyExtractor}.
     * <p>
     * The order of elements in the resulting list is preserved from the original collection.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var users = List.of(new User(1, "A"), new User(2, "B"), new User(1, "C"));
     * JXCollections.distinctBy(users, User::id);
     * // Returns: [User(1, "A"), User(2, "B")]
     * }</pre></blockquote>
     *
     * @param collection The collection to process.
     * @param keyExtractor A function to extract the key for uniqueness comparison.
     * @param <T> The type of elements in the collection.
     * @param <K> The type of the key.
     * @return A new list with distinct elements based on the key.
     */
    public static <T, K> List<T> distinctBy(
            Collection<T> collection,
            Function<T, K> keyExtractor
    ) {
        if (collection == null || collection.isEmpty()) {
            return List.of();
        }
        Objects.requireNonNull(keyExtractor, "keyExtractor cannot be null");

        final var seenKeys = new HashSet<K>();
        final var result = new ArrayList<T>();

        for (T element : collection) {
            K key = keyExtractor.apply(element);
            if (seenKeys.add(key)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Splits this collection into a list of lists, each not exceeding the given {@code size}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var numbers = List.of(1, 2, 3, 4, 5);
     * JXCollections.chunked(numbers, 2); // Returns: [[1, 2], [3, 4], [5]]
     * }</pre></blockquote>
     *
     * @param collection The collection to split.
     * @param size The desired size of each chunk. Must be positive.
     * @param <T> The type of elements in the collection.
     * @return A list of lists.
     * @throws IllegalArgumentException if the size is not positive.
     */
    public static <T> List<List<T>> chunked(Collection<T> collection, int size) {
        if (collection == null || collection.isEmpty()) {
            return List.of();
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive.");
        }

        final List<List<T>> chunks = new ArrayList<>();
        List<T> currentChunk = new ArrayList<>(size);
        for (T element : collection) {
            currentChunk.add(element);
            if (currentChunk.size() == size) {
                chunks.add(currentChunk);
                currentChunk = new ArrayList<>(size);
            }
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk);
        }
        return chunks;
    }

    /**
     * Returns a new list sorted according to the natural order of the keys
     * extracted by the given {@code keyExtractor}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var users = List.of(new User("Bob", 30), new User("Alice", 25));
     * JXCollections.sortBy(users, User::age); // Returns users sorted by age
     * }</pre></blockquote>
     *
     * @param collection   The collection to sort.
     * @param keyExtractor A function to extract a {@link Comparable} sort key from an element.
     * @param <T>          The type of elements in the collection.
     * @param <K>          The type of the sort key.
     * @return A new, sorted list.
     */
    public static <T, K extends Comparable<? super K>> List<T> sortBy(
            Collection<T> collection,
            Function<T, K> keyExtractor
    ) {
        if (collection == null || collection.isEmpty()) {
            return List.of();
        }
        Objects.requireNonNull(keyExtractor, "keyExtractor cannot be null");

        return collection
                .stream()
                .sorted(Comparator.comparing(keyExtractor))
                .toList();
    }

    /**
     * Returns a new list sorted according to the given {@link Comparator}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var names = List.of("Chris", "Alice", "Bob");
     * JXCollections.sortBy(names, Comparator.comparing(String::length));
     * // Returns: ["Bob", "Alice", "Chris"]
     * }</pre></blockquote>
     *
     * @param collection The collection to sort.
     * @param comparator The comparator to determine the order.
     * @param <T>        The type of elements in the collection.
     * @return A new, sorted list.
     */
    public static <T> List<T> sortBy(
            Collection<T> collection,
            Comparator<? super T> comparator
    ) {
        if (collection == null || collection.isEmpty()) {
            return List.of();
        }
        Objects.requireNonNull(comparator, "comparator cannot be null");

        return collection
                .stream()
                .sorted(comparator)
                .toList();
    }

    /**
     * Returns a new list sorted according to the natural order of its elements.
     * The elements must implement the {@link Comparable} interface.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var numbers = List.of(3, 1, 2);
     * JXCollections.sortBy(numbers); // Returns: [1, 2, 3]
     * }</pre></blockquote>
     *
     * @param collection The collection to sort.
     * @param <T>        The type of elements in the collection, which must be comparable.
     * @return A new, sorted list.
     */
    public static <T extends Comparable<? super T>> List<T> sortBy(
            Collection<T> collection
    ) {
        if (collection == null || collection.isEmpty()) {
            return List.of();
        }
        return collection.stream()
                .sorted()
                .toList();
    }

    /**
     * Returns the first element yielding the smallest value of the given function.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var users = List.of(new User("Bob", 30), new User("Alice", 25));
     * JXCollections.minBy(users, User::age); // Maybe.just(User("Alice", 25))
     * }</pre></blockquote>
     *
     * @param collection   The collection to search in.
     * @param keyExtractor A function to extract a {@link Comparable} key from an element.
     * @param <T>          The type of elements in the collection.
     * @param <K>          The type of the key.
     * @return A {@code Maybe.Just} containing the element with the minimum key,
     * or {@code Maybe.Nothing} if the collection is empty.
     */
    public static <T, K extends Comparable<? super K>> Maybe<T> minBy(
            Collection<T> collection,
            Function<T, K> keyExtractor
    ) {
        if (collection == null || collection.isEmpty()) {
            return Maybe.nothing();
        }
        Objects.requireNonNull(keyExtractor, "keyExtractor cannot be null");

        return Maybe.ofNullable(
                collection.stream()
                        .min(Comparator.comparing(keyExtractor))
                        .orElse(null)
        );
    }

    /**
     * Returns the first element having the smallest value according to the given {@link Comparator}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var names = List.of("Christopher", "Al", "Bob");
     * JXCollections.minBy(names, Comparator.comparing(String::length)); // Maybe.just("Al")
     * }</pre></blockquote>
     *
     * @param collection The collection to search in.
     * @param comparator The comparator to determine the order.
     * @param <T>        The type of elements in the collection.
     * @return A {@code Maybe.Just} containing the minimum element,
     * or {@code Maybe.Nothing} if the collection is empty.
     */
    public static <T> Maybe<T> minBy(
            Collection<T> collection,
            Comparator<? super T> comparator
    ) {
        if (collection == null || collection.isEmpty()) {
            return Maybe.nothing();
        }
        Objects.requireNonNull(comparator, "comparator cannot be null");

        return Maybe.ofNullable(
                collection.stream().min(comparator).orElse(null)
        );
    }

    /**
     * Returns the first element yielding the largest value of the given function.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var users = List.of(new User("Bob", 30), new User("Alice", 25));
     * JXCollections.maxBy(users, User::age); // Maybe.just(User("Bob", 30))
     * }</pre></blockquote>
     *
     * @param collection   The collection to search in.
     * @param keyExtractor A function to extract a {@link Comparable} key from an element.
     * @param <T>          The type of elements in the collection.
     * @param <K>          The type of the key.
     * @return A {@code Maybe.Just} containing the element with the maximum key,
     * or {@code Maybe.Nothing} if the collection is empty.
     */
    public static <T, K extends Comparable<? super K>> Maybe<T> maxBy(
            Collection<T> collection,
            Function<T, K> keyExtractor
    ) {
        if (collection == null || collection.isEmpty()) {
            return Maybe.nothing();
        }
        Objects.requireNonNull(keyExtractor, "keyExtractor cannot be null");

        return Maybe.ofNullable(
                collection.stream()
                        .max(Comparator.comparing(keyExtractor))
                        .orElse(null)
        );
    }

    /**
     * Returns the first element having the largest value according to the given {@link Comparator}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * var names = List.of("Christopher", "Al", "Bob");
     * JXCollections.maxBy(names, Comparator.comparing(String::length)); // Maybe.just("Christopher")
     * }</pre></blockquote>
     *
     * @param collection The collection to search in.
     * @param comparator The comparator to determine the order.
     * @param <T>        The type of elements in the collection.
     * @return A {@code Maybe.Just} containing the maximum element,
     * or {@code Maybe.Nothing} if the collection is empty.
     */
    public static <T> Maybe<T> maxBy(
            Collection<T> collection,
            Comparator<? super T> comparator
    ) {
        if (collection == null || collection.isEmpty()) {
            return Maybe.nothing();
        }
        Objects.requireNonNull(comparator, "comparator cannot be null");

        return Maybe.ofNullable(
                collection.stream().max(comparator).orElse(null)
        );
    }

    /**
     * Groups elements of the original collection by a key returned by the given {@code keyExtractor}.
     *
     * <h4>Example of Usage:</h4>
     * <blockquote><pre>{@code
     * record User(String name, String city) {}
     * var users = List.of(new User("Alice", "Prague"), new User("Bob", "London"));
     * Map<String, List<User>> usersByCity = JXCollections.groupBy(users, User::city);
     * // usersByCity is {"Prague"=[User("Alice", "Prague")], "London"=[User("Bob", "London")]}
     * }</pre></blockquote>
     *
     * @param collection   The collection to group.
     * @param keyExtractor A function to extract the key for grouping.
     * @param <T>          The type of elements in the collection.
     * @param <K>          The type of the key.
     * @return A {@code Map} where keys are the results of the key extractor and values are lists of elements.
     */
    public static <T, K> Map<K, List<T>> groupBy(
            Collection<T> collection,
            Function<T, K> keyExtractor
    ) {
        if (collection == null || collection.isEmpty()) {
            return Map.of();
        }
        Objects.requireNonNull(keyExtractor, "keyExtractor cannot be null");

        return collection.stream().collect(Collectors.groupingBy(keyExtractor));
    }
}