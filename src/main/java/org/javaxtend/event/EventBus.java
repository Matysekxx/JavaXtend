package org.javaxtend.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A simple, thread-safe event bus for decoupled, in-process communication.
 * <p>
 * It allows different parts of an application to communicate by publishing and
 * subscribing to events without having direct knowledge of each other.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>{@code
 * // 1. Define an event class
 * record UserLoggedInEvent(String username) {}
 *
 * // 2. Create an EventBus instance
 * EventBus eventBus = new EventBus();
 *
 * // 3. Subscribe to the event
 * eventBus.subscribe(UserLoggedInEvent.class, event -> {
 *     System.out.println("Welcome, " + event.username());
 * });
 *
 * // 4. Publish the event from somewhere else in the application
 * eventBus.publish(new UserLoggedInEvent("JohnDoe"));
 * }</pre></blockquote>
 */
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Subscribes a handler to a specific event type.
     *
     * @param eventType The class of the event to subscribe to.
     * @param handler   The consumer that will handle the event.
     * @param <T>       The type of the event.
     */
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        final List<Consumer<?>> handlers = subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());
        handlers.add(handler);
    }

    /**
     * Publishes an event to all registered subscribers.
     *
     * @param event The event object to publish.
     */
    @SuppressWarnings("unchecked")
    public void publish(Object event) {
        if (event == null) {
            return;
        }
        final List<Consumer<?>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            handlers.forEach(
                    handler -> ((Consumer<Object>) handler).accept(event)
            );
        }
    }
}