package com.mrgautam;

import javax.swing.SwingUtilities;

import com.mrgautam.ui.MusicPlayerUI;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class Main {
    public static void main(String[] args) {

        // Initialize JavaFX Toolkit first
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            Platform.setImplicitExit(false); // Prevent JavaFX from shutting down
            
            // Now create and show your Swing UI
            MusicPlayerUI musicPlayerUI = new MusicPlayerUI();

            musicPlayerUI.setVisible(true);
// 
            // NotificationService nService = new NotificationService();
            // nService.openAppMail();
        });
        // SongService songService = new SongService();
        // Song song =songService.getSongById(15);
        // song.addSongImage("C:\\Users\\Lenovo\\Downloads\\hwDTMS4oo6A-HD.jpg");
        


    }
}



