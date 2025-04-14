package com.mrgautam.player;

import java.sql.SQLException;

import com.mrgautam.model.Song;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;

    public void play(int songId, Runnable onEnd) {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        try {
            Media media = Song.getMedia(songId);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnEndOfMedia(onEnd);
            mediaPlayer.play();
            isPaused = false;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, e.g., show an error message or log it
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            isPaused = false;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false;
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) mediaPlayer.setVolume(volume); // 0.0 to 1.0
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public Duration getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.getCurrentTime() : Duration.ZERO;
    }
    
    public Duration getTotalDuration() {
        return mediaPlayer != null ? mediaPlayer.getTotalDuration() : Duration.UNKNOWN;
    }
    
    public boolean isSeekable() {
        return mediaPlayer != null && mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO);
    }
    
    public void seekTo(double percent) {
        if (mediaPlayer != null) {
            Duration total = mediaPlayer.getTotalDuration();
            Duration seekTo = total.multiply(percent);
            mediaPlayer.seek(seekTo);
        }
    }
    
}
