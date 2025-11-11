package org.javaxtend.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class EventBusTest {

    private EventBus eventBus;
    private record StringEvent(String message) {}
    private record IntegerEvent(int value) {}

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
    }

    @Test
    @DisplayName("should deliver an event to a single subscriber")
    void publish_deliversToSingleSubscriber() {
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        eventBus.subscribe(StringEvent.class, event -> receivedMessage.set(event.message()));
        eventBus.publish(new StringEvent("Hello, World!"));

        assertEquals("Hello, World!", receivedMessage.get());
    }

    @Test
    @DisplayName("should deliver an event to multiple subscribers")
    void publish_deliversToMultipleSubscribers() {
        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);

        eventBus.subscribe(IntegerEvent.class, event -> counter1.addAndGet(event.value()));
        eventBus.subscribe(IntegerEvent.class, event -> counter2.addAndGet(event.value() * 2));

        eventBus.publish(new IntegerEvent(10));

        assertEquals(10, counter1.get());
        assertEquals(20, counter2.get());
    }

    @Test
    @DisplayName("should not deliver an event to subscribers of a different type")
    void publish_doesNotDeliverToWrongType() {
        AtomicBoolean stringSubscriberCalled = new AtomicBoolean(false);
        eventBus.subscribe(StringEvent.class, event -> stringSubscriberCalled.set(true));
        eventBus.publish(new IntegerEvent(123));
        assertFalse(stringSubscriberCalled.get());
    }

    @Test
    @DisplayName("should do nothing when publishing an event with no subscribers")
    void publish_noSubscribers() {
        assertDoesNotThrow(() -> eventBus.publish(new StringEvent("No one is listening")));
    }
}