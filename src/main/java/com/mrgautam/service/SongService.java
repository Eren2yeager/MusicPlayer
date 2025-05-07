package com.mrgautam.service;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mrgautam.database.DBManager;
import com.mrgautam.model.Song;

public class SongService {

    // Fetch all songs from the database
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();

        String query = "SELECT * FROM songs";

        try (Connection conn = DBManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Song song = new Song(rs.getInt("id"),rs.getString("title"),rs.getString("artist"),rs.getString("filepath"));
                songs.add(song);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    // Add a song to the database
    public boolean addSong(Song song) {
        String query = "INSERT INTO songs (title, artist, filepath, audio_data) VALUES (?, ?, ?,?)";

        try (Connection conn = DBManager.getConnection(); FileInputStream inputStream = new FileInputStream(song.getFilePath()); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getFilePath());
            stmt.setBinaryStream(4, inputStream, inputStream.available()); // Assuming you have a method to get the
            // audio data as InputStream

            int affected = stmt.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Song> getSearchedSongs(String searchQuery) {
        List<Song> searchedSongs = new ArrayList<>();

        String query = "SELECT * FROM songs WHERE title LIKE \"" + searchQuery + "%\" OR artist LIKE \"" + searchQuery
                + "%\"";

        try (Connection conn = DBManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("filepath"));
                searchedSongs.add(song);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchedSongs;
    }

    public Song getSongById(int id) {
        Song song = null;
        String query = "SELECT * FROM songs WHERE id = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("filepath"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }

    public int getLatestSongId() {
        String query = "SELECT MAX(id) as max_id FROM songs";
        try (Connection conn = DBManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // error fallback
    }

    public Boolean addLastSong(Song lastSong) {
        String deleteQuery = "DELETE FROM lastsong;";
        String insertQuery = "INSERT INTO lastsong (id, title, artist) VALUES (?, ?, ?);";
        try (Connection conn = DBManager.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.executeUpdate();
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, lastSong.getId());
                insertStmt.setString(2, lastSong.getTitle());
                insertStmt.setString(3, lastSong.getArtist());
                return insertStmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public int getLastSongIndex() {
        String query = "SELECT id  FROM lastsong WHERE id =(SELECT MAX(id) FROM lastsong);";
        try (Connection conn = DBManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            int lastSongId = -1;
            if (rs.next()) {
                lastSongId = rs.getInt("id");
            }
            return lastSongId;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1; // error fallback
        }
    }

    public Boolean deleteSong(Song song) {
        String deleteQuery = "DELETE FROM songs WHERE id = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            int id = song.getId();
            System.out.println("Attempting to delete song with ID: " + id);

            deleteStmt.setInt(1, id);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("⚠️ No song was deleted. ID might not exist.");
            } else {
                System.out.println("✅ Song deleted successfully.");
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
