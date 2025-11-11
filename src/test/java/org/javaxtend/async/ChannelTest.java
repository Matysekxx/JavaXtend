package org.javaxtend.async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ChannelTest {

    @Test
    @DisplayName("should send and receive a single item")
    void sendAndReceive_singleItem() throws InterruptedException {
        Channel<String> channel = new Channel<>(1);
        channel.send("hello");
        String result = channel.receive().unwrap();
        assertEquals("hello", result);
    }

    @Test
    @DisplayName("iterator should receive all sent items and terminate")
    @Timeout(1)
    void iterator_receivesAllItems() {
        Channel<Integer> channel = new Channel<>(5);
        List<Integer> receivedItems = new ArrayList<>();

        Async.of(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    channel.send(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                channel.close();
            }
            return null;
        });

        for (Integer item : channel) {
            receivedItems.add(item);
        }

        assertEquals(List.of(0, 1, 2), receivedItems);
    }

    @Test
    @DisplayName("send should block when channel is full")
    @Timeout(1)
    void send_blocksWhenFull() throws InterruptedException {
        Channel<Integer> channel = new Channel<>(1);
        channel.send(1);

        AtomicBoolean sendCompleted = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try {
                channel.send(2);
                sendCompleted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        Thread.sleep(100);
        assertFalse(sendCompleted.get(), "Send should be blocked");

        channel.receive();
        producer.join(100);

        assertTrue(sendCompleted.get(), "Send should have completed after an item was received");
    }

    @Test
    @DisplayName("receive should block when channel is empty")
    @Timeout(1)
    void receive_blocksWhenEmpty() throws InterruptedException {
        Channel<Integer> channel = new Channel<>(1);
        AtomicBoolean receiveCompleted = new AtomicBoolean(false);

        Thread consumer = new Thread(() -> {
            try {
                channel.receive();
                receiveCompleted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        Thread.sleep(100);
        assertFalse(receiveCompleted.get(), "Receive should be blocked");

        channel.send(1);
        consumer.join(100);

        assertTrue(receiveCompleted.get(), "Receive should have completed after an item was sent");
    }

    @Test
    @DisplayName("should not allow sending on a closed channel")
    void send_onClosedChannel_throwsException() {
        Channel<String> channel = new Channel<>(1);
        channel.close();
        assertThrows(IllegalStateException.class, () -> channel.send("test"));
    }

    @Test
    @DisplayName("should handle multiple producers and a single consumer")
    @Timeout(2)
    void multipleProducers_singleConsumer() throws InterruptedException {
        Channel<Integer> channel = new Channel<>(10);
        int numProducers = 5;
        int itemsPerProducer = 20;
        int totalItems = numProducers * itemsPerProducer;
        ExecutorService executor = Executors.newFixedThreadPool(numProducers);
        CountDownLatch producersLatch = new CountDownLatch(numProducers);
        List<Integer> receivedItems = new ArrayList<>();

        Thread consumerThread = new Thread(() -> {
            for (Integer item : channel) {
                receivedItems.add(item);
            }
        });
        consumerThread.start();

        for (int i = 0; i < numProducers; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        channel.send(1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    producersLatch.countDown();
                }
            });
        }

        producersLatch.await();
        channel.close();
        consumerThread.join();

        assertEquals(totalItems, receivedItems.size());
        executor.shutdown();
    }
}