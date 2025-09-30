package org.javaxtend.audio;

/**
 * A static utility class for managing background music playback.
 * It acts like a "radio" that can only play one song at a time,
 * allowing for global control over the music.
 */
public final class MusicPlayer {

    private static Sound currentMusic;
    private MusicPlayer() {}

    /**
     * Plays a new music track. If another track is already playing, it will be stopped and replaced.
     *
     * @param music The {@link Sound} object to be played as music.
     */
    public static void play(final Sound music) {
        if (music == null) {
            return;
        }
        stop();

        currentMusic = music;
        currentMusic.loop();
    }

    /**
     * Stops the currently playing music and releases its resources.
     */
    public static void stop() {
        if (currentMusic != null) {
            currentMusic.close();
            currentMusic = null;
        }
    }

    /**
     * Pauses the currently playing music.
     */
    public static void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    /**
     * Resumes the paused music.
     */
    public static void resume() {
        if (currentMusic != null) {
            currentMusic.resume();
        }
    }

    /**
     * Checks if music is currently playing.
     * @return true if music is playing, false otherwise.
     */
    public static boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }
}