package org.javxtend.util;

import java.util.List;
import java.util.Objects;

/**
 * A mutable, general-purpose implementation of the {@link Tuple3} interface.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     // Create a triple to hold a point in 3D space
 *     JXTuple3&lt;Integer, Integer, Integer&gt; point = JXTuple3.of(10, 20, 30);
 *
 *     // Access the elements
 *     int x = point.getFirst();
 *     int y = point.getSecond();
 *     int z = point.getThird();
 *
 *     // Modify an element
 *     point.setThird(35);
 * </pre></blockquote>
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 */
public class JXTuple3<T1, T2, T3> implements Tuple3<T1, T2, T3> {

    private T1 first;
    private T2 second;
    private T3 third;

    public JXTuple3(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T1, T2, T3> JXTuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new JXTuple3<>(first, second, third);
    }

    @Override
    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    @Override
    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public T3 getThird() {
        return third;
    }

    public void setThird(T3 third) {
        this.third = third;
    }

    @Override
    public List<Object> toList() {
        return List.of(first, second, third);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JXTuple3<?, ?, ?> jxTuple3 = (JXTuple3<?, ?, ?>) o;
        return Objects.equals(first, jxTuple3.first) &&
               Objects.equals(second, jxTuple3.second) &&
               Objects.equals(third, jxTuple3.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}