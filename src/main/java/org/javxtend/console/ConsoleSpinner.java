package org.javxtend.console;

import org.javxtend.io.IO;

public class ConsoleSpinner {

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
        public char[] getFrames() { return frames; }
    }
    private final String message;
    private final char[] frames;
    private volatile boolean spinning = false;
    private Thread thread;
    private int interval = 100;

    public ConsoleSpinner(String message, Animation animation) {
        this.message = message;
        this.frames = animation.getFrames();
    }
    public ConsoleSpinner(String message, char[] customFrames) {
        this.message = message;
        this.frames = customFrames;
    }
    public void setInterval(int interval) { this.interval = interval; }
    public void start() {
        spinning = true;
        thread = new Thread(() -> {
            int i = 0;
            while (spinning) {
                System.out.print("\r" + message + " " + frames[i % frames.length]);
                i++;
                try { Thread.sleep(interval); } catch (InterruptedException ignored) {}
            }
        });
        thread.start();
    }

    public void stop() { stop(null); }

    public void stop(String finalMessage) {
        spinning = false;
        try { thread.join(); } catch (InterruptedException ignored) {}
        String doneMsg = (finalMessage != null) ? finalMessage : "Done!";
        System.out.println("\r" + message + " ✓ " + doneMsg);
    }
}
