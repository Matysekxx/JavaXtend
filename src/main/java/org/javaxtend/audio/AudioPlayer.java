package org.javaxtend.audio;

import org.javaxtend.io.IO;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * A simple utility class for playing audio files.
 * Supports formats like WAV, AU, AIFF.
 */
public class AudioPlayer {

    /**
     * Plays a sound effect from the given file path.
     * This method is "fire and forget" - it plays the sound in the background
     * and does not block the execution of your program.
     *
     * @param filePath The path to the audio file.
     */
    public static void playSound(final String filePath) {
        final Sound sound = loadSound(filePath);
        if (sound != null) {
            sound.clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });
            sound.play();
        }
    }

    /**
     * Loads an audio file into a {@link Sound} object for manual control (play, stop, loop).
     * This is ideal for background music or sounds you need to manage.
     * Returns null if the sound cannot be loaded.
     *
     * @param filePath The path to the audio file.
     * @return A {@link Sound} object, or null if an error occurred.
     */
    public static Sound loadSound(final String filePath) {
        try {
            final var audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + filePath);
                return null;
            }

            final var audioStream = AudioSystem.getAudioInputStream(audioFile);
            final var clip = AudioSystem.getClip();
            clip.open(audioStream);
            return new Sound(clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            IO.printlnERR("Error playing sound: " + e.getMessage());
            return null;
        }
    }
}