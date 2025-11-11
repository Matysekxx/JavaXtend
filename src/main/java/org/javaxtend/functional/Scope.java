package org.javaxtend.functional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A utility class providing scope functions inspired by Kotlin.
 * These functions execute a block of code within the context of an object,
 * improving code readability and fluency.
 */
public final class Scope {
    private Scope() {}

    /**
     * Calls the specified function {@code block} with {@code this} value as its receiver and returns {@code this} value.
     * <p>
     * Best used for object configuration.
     *
     * @param object The object to apply the block to.
     * @param block  The block of code to execute on the object.
     * @param <T>    The type of the object.
     * @return The original object.
     */
    public static <T> T apply(T object, Consumer<T> block) {
        block.accept(object);
        return object;
    }

    /**
     * Calls the specified function {@code block} with {@code this} value as its argument and returns its result.
     * <p>
     * Best used for transforming an object or executing a block of code on a non-null object.
     *
     * @param object The object to pass to the block.
     * @param block  The block of code to execute.
     * @param <T>    The type of the input object.
     * @param <R>    The type of the result.
     * @return The result of the block execution.
     */
    public static <T, R> R let(T object, Function<T, R> block) {
        return block.apply(object);
    }

    /**
     * Calls the specified function {@code block} with {@code this} value as its argument and returns {@code this} value.
     * <p>
     * Best used for performing side-effects like logging or debugging without modifying the object.
     * The name "also" implies: "do something with the object, and *also* do this".
     *
     * @param object The object to pass to the block.
     * @param block  The block of code to execute.
     * @param <T>    The type of the object.
     * @return The original object.
     */
    public static <T> T also(T object, Consumer<T> block) {
        block.accept(object);
        return object;
    }


    /**
     * Executes the given block of code and returns its result.
     * <p>
     * Best used for creating a local scope for a group of statements to compute a single result.
     *
     * @param block The block of code to execute.
     * @param <R>   The type of the result.
     * @return The result of the block execution.
     */
    public static <R> R run(Supplier<R> block) {
        return block.get();
    }

    /**
     * Returns {@code this} value if it satisfies the given {@code predicate} or {@code null} otherwise.
     * <p>
     * Useful for chaining checks in a fluent way, often in combination with {@code Maybe}.
     *
     * @param object    The object to check.
     * @param predicate The condition to check.
     * @param <T>       The type of the object.
     * @return The original object if the predicate is true; {@code null} otherwise.
     */
    public static <T> T takeIf(T object, Predicate<T> predicate) {
        if (object != null && predicate.test(object)) {
            return object;
        }
        return null;
    }

    /**
     * Returns {@code this} value if it does not satisfy the given {@code predicate} or {@code null} otherwise.
     * <p>
     * Useful for chaining checks in a fluent way, often in combination with {@code Maybe}.
     *
     * @param object    The object to check.
     * @param predicate The condition to check.
     * @param <T>       The type of the object.
     * @return The original object if the predicate is false; {@code null} otherwise.
     */
    public static <T> T takeUnless(T object, Predicate<T> predicate) {
        if (object != null && !predicate.test(object)) {
            return object;
        }
        return null;
    }
}
