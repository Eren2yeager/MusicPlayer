package com.mrgautam;

import com.mrgautam.ui.MusicPlayerUI;

import javafx.embed.swing.JFXPanel;

public class Main {
    public static void main(String[] args) {
        //1 --------------------------------------------------------------------------------------------------------------------------
        // DBManager.testConnection(); // IT WAS TO CHECK THE DB CONNECTION
        // --------------------------------------------------------------------------------------------------------------------------
        
        //2 --------------------------------------------------------------------------------------------------------------------------

        // SongService service = new SongService();

        // // Uncomment this to add a song

        // /* 
        // Song newSong = new Song(0, "Love The Way You Lie ft. Rihanna", "Eminem", "c:\\Users\\Lenovo\\Downloads\\Eminem - Love The Way You Lie ft. Rihanna (1).mp3");
        // boolean success = service.addSong(newSong);
        // System.out.println(success ? "✅ Song added!" : "❌ Failed to add song.");
        // */

        // Fetch all songs
        // List<Song> songs = service.getAllSongs();
        // for (Song song : songs) {
        //     System.out.println(song.getId() +". "+ song);
        // }
        // --------------------------------------------------------------------------------------------------------------------------

        //3. for simple song player (does not load next song when end) --------------------------------------------------------------------------------------------------------------------------

        // SongService service = new SongService();
        // AudioPlayer player = new AudioPlayer();
        
        // List<Song> songs = service.getAllSongs();
        // if (!songs.isEmpty()) {
        //     Song first = songs.get(2);
        //     System.out.println("🎶 Now playing: " + first);
        //     player.play(first.getFilePath());

        //     } else {
        //      System.out.println("⚠️ No songs found.");
        //     }
        
        //4. autoplay --------------------------------------------------------------------------------------------------------------------------
    //     SongService service = new SongService();
    //     songs = service.getAllSongs();

    //     if (songs.isEmpty()) {
    //         System.out.println("⚠️ No songs found.");
    //         return;
    //     }

    //     playCurrentSong();
    // }

    // private static void playCurrentSong() {
    //     if (currentSongIndex >= songs.size()) {
    //         System.out.println("✅ Playlist ended.");
    //         return;
    //     }

    //     Song current = songs.get(currentSongIndex);
    //     System.out.println("🎶 Now playing: " + current);

    //     player.play(current.getFilePath(), () -> {
    //         currentSongIndex++;
    //         playCurrentSong();  // autoplay next
    //     });
    //5. swift invoke --------------------------------------------------------------------------------------------------------------------------
    
    //   SwingUtilities.invokeLater(() -> {
    //         new MusicPlayerUI().setVisible(true);
    //     });

 new JFXPanel();

        // Launch the Swing UI
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MusicPlayerUI().setVisible(true);
        });



    
        }

                           //for autoplay
        // private static int currentSongIndex = 0;
        // private static List<Song> songs;
        // private static AudioPlayer player = new AudioPlayer();

        

}



