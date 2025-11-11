package org.javaxtend.functional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScopeTest {

    private static class MutablePerson {
        String name;
        int age;
    }

    @Test
    @DisplayName("apply() should configure the object and return it")
    void apply_configuresAndReturnsObject() {
        MutablePerson person = new MutablePerson();

        MutablePerson configuredPerson = Scope.apply(person, p -> {
            p.name = "John";
            p.age = 30;
        });

        assertSame(person, configuredPerson, "apply() should return the same instance.");
        assertEquals("John", person.name);
        assertEquals(30, person.age);
    }

    @Test
    @DisplayName("let() should transform the object and return the result")
    void let_transformsAndReturnsResult() {
        String name = "John";

        Integer length = Scope.let(name, String::length);

        assertEquals(4, length);

        String greeting = Scope.let(name, n -> "Hello, " + n);
        assertEquals("Hello, John", greeting);
    }

    @Test
    @DisplayName("also() should perform a side-effect and return the original object")
    void also_performsSideEffectAndReturnsObject() {
        List<String> names = new ArrayList<>();
        names.add("Alice");

        List<String> returnedList = Scope.also(names, list -> {
            System.out.println("List size is: " + list.size());
            list.add("Bob");
        });

        assertSame(names, returnedList, "also() should return the same instance.");
        assertEquals(2, names.size());
        assertEquals(List.of("Alice", "Bob"), names);
    }

    @Test
    @DisplayName("run() should execute the block and return its result")
    void run_executesAndReturnsResult() {
        boolean isProduction = false;

        String connectionString = Scope.run(() -> {
            if (isProduction) {
                return "prod_connection";
            } else {
                return "dev_connection";
            }
        });

        assertEquals("dev_connection", connectionString);
    }

    @Test
    @DisplayName("takeIf() should return the object if predicate is true, otherwise null")
    void takeIf_returnsObject() {
        String valid = "valid";
        String invalid = "no";

        String result1 = Scope.takeIf(valid, s -> s.length() > 3);
        String result2 = Scope.takeIf(invalid, s -> s.length() > 3);

        assertSame(valid, result1);
        assertNull(result2);
    }

    @Test
    @DisplayName("takeUnless() should return the object if predicate is false, otherwise null")
    void takeUnless_returnsObject() {
        String valid = "valid";
        String invalid = "no";

        String result1 = Scope.takeUnless(invalid, s -> s.length() > 3);
        String result2 = Scope.takeUnless(valid, s -> s.length() > 3);

        assertSame(invalid, result1);
        assertNull(result2);
    }
}