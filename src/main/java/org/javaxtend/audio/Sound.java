package org.javaxtend.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

/**
 * Represents a loaded audio clip that can be played, stopped, or looped.
 * This object should be created via the {@link AudioPlayer} class.
 */
public class Sound {

    final Clip clip;
    private long pausedPosition = 0;

    /**
     * Internal constructor. Use AudioPlayer.loadSound() to create an instance.
     * @param clip The preloaded audio clip.
     */
    Sound(final Clip clip) {
        this.clip = clip;
    }

    /**
     * Plays the sound once from the beginning.
     * If the sound is already playing, it will be restarted.
     */
    public void play() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Stops the sound playback.
     */
    public void stop() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        this.pausedPosition = 0;
    }

    /**
     * Pauses the sound playback at its current position.
     */
    public void pause() {
        if (isPlaying()) {
            this.pausedPosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    /**
     * Resumes playback from where it was paused.
     */
    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.setMicrosecondPosition(this.pausedPosition);
            clip.start();
        }
    }

    /**
     * Loops the sound continuously.
     * The playback will start from the beginning.
     */
    public void loop() {
        loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Loops the sound a specific number of times.
     *
     * @param count The number of times to repeat the loop.
     *              For example, 0 plays it once, 1 plays it twice.
     */
    public void loop(final int count) {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(count);
    }

    /**
     * Checks if the sound is currently playing.
     * @return true if the sound is playing, false otherwise.
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Releases the system resources used by this sound clip.
     * After closing, the sound can no longer be played.
     * It's good practice to call this when you are done with a sound object.
     */
    public void close() {
        if (clip == null) return;
        if (clip.isOpen()) {
            clip.stop();
            clip.close();
        }
    }
}