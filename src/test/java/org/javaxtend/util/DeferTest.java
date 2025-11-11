package org.javaxtend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeferTest {

    @Test
    @DisplayName("should execute deferred actions in LIFO order")
    void defer_executesInLifoOrder() {
        final List<String> executionOrder = new ArrayList<>();

        try (var d = Defer.create()) {
            d.defer(() -> executionOrder.add("first"));
            d.defer(() -> executionOrder.add("second"));
        }

        assertEquals(List.of("second", "first"), executionOrder);
    }

    @Test
    @DisplayName("should execute deferred actions even if an exception is thrown")
    void defer_executesOnException() {
        final List<String> executionOrder = new ArrayList<>();

        assertThrows(RuntimeException.class, () -> {
            try (var d = Defer.create()) {
                d.defer(() -> executionOrder.add("cleanup"));
                throw new RuntimeException("test exception");
            }
        });

        assertEquals(List.of("cleanup"), executionOrder);
    }

    @Test
    @DisplayName("should handle no deferred actions gracefully")
    void defer_handlesNoActions() {
        try (var d = Defer.create()) {
        }
    }
}