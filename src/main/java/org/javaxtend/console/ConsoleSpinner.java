package org.javaxtend.console;

import org.javaxtend.io.IO;

/**
 * A utility for displaying a simple, animated "spinner" in the console.
 * <p>
 * This is useful for indicating that a long-running task is in progress. The spinner
 * runs in a separate thread and can be started and stopped as needed.
 *
 * <h2>Example of Usage:</h2>
 * <blockquote><pre>
 *     ConsoleSpinner spinner = new ConsoleSpinner("Processing data...", ConsoleSpinner.Animation.CLASSIC);
 *     spinner.start();
 *     // Simulate a long task
 *     try {
 *         Thread.sleep(5000);
 *     } catch (InterruptedException e) {
 *         Thread.currentThread().interrupt();
 *     }
 *     spinner.stop("Task completed successfully!");
 * </pre></blockquote>
 */
public class ConsoleSpinner {

    /**
     * Predefined sets of characters for different spinner animations.
     */
    public enum Animation {
        CLASSIC(new char[]{'|', '/', '-', '\\'}),
        DOTS(new char[]{'.', 'o', 'O', '@', '*'}),
        ARROWS(new char[]{'←', '↑', '→', '↓'}),
        BLOCKS(new char[]{'▁', '▃', '▄', '▅', '▆', '▇', '█'}),
        SHADES(new char[]{'░','▒','▓','█'}),
        CIRCLES(new char[]{'◐','◓','◑','◒'}),
        TRIANGLES(new char[]{'▲','▶','▼','◀'}),
        SQUARES(new char[]{'▖','▘','▝','▗'}),
        CHINESE_STEPS(new char[]{'一','二','三','四','五','六','七','八','九','十'}),
        ARROWS2(new char[]{'⇐','⇑','⇒','⇓'}),
        STARS(new char[]{'✦', '✧', '✶', '✷'});

        private final char[] frames;
        Animation(char[] frames) { this.frames = frames; }

        /**
         * @return A copy of the animation frames.
         */
        public char[] getFrames() { return frames.clone(); }
    }
    /**
     * The message displayed next to the spinner.
     */
    private final String message;
    /**
     * The character frames for the spinner animation.
     */
    private final char[] frames;
    /**
     * A volatile flag to control the spinning state of the animation thread.
     */
    private volatile boolean spinning = false;
    /**
     * The thread that runs the spinner animation.
     */
    private Thread thread;
    /**
     * The delay in milliseconds between animation frames.
     */
    private int interval = 100;

    /**
     * Constructs a spinner with a message and a predefined animation style.
     * @param message The message to display next to the spinner.
     * @param animation The predefined animation to use.
     */
    public ConsoleSpinner(String message, Animation animation) {
        this.message = message;
        this.frames = animation.getFrames();
    }

    /**
     * Constructs a spinner with a message and a custom set of animation frames.
     * @param message The message to display next to the spinner.
     * @param customFrames An array of characters representing the animation frames.
     */
    public ConsoleSpinner(String message, char[] customFrames) {
        this.message = message;
        this.frames = customFrames;
    }

    /**
     * Sets the refresh interval for the spinner animation.
     * @param interval The time in milliseconds between each frame.
     */
    public void setInterval(int interval) { this.interval = interval; }

    /**
     * Starts the spinner animation in a new thread.
     */
    public void start() {
        spinning = true;
        thread = new Thread(() -> {
            int i = 0;
            while (spinning) {
                IO.printLive("\r" + message + " " + frames[i % frames.length]);
                i++;
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    /**
     * Stops the spinner and displays a default final message ("Done!").
     */
    public void stop() { stop(null); }

    /**
     * Stops the spinner and displays a custom final message.
     * @param finalMessage The message to display after the spinner stops. If null, a default message is used.
     */
    public void stop(String finalMessage) {
        spinning = false;
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String doneMsg = (finalMessage != null) ? finalMessage : "Done!";
        IO.println("\r" + message + " " + ConsoleColors.GREEN.colorize("✓") + " " + doneMsg);
    }
}
