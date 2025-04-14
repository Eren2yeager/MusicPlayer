package com.mrgautam.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrgautam.database.DBManager;

import javafx.scene.media.Media;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String filePath;

    public Song(int id, String title, String artist, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static Media getMedia(int songId) throws SQLException {

        Media media = null; // Declare media variable here

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT audio_data FROM songs WHERE id = ?");
            ps.setInt(1, songId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                InputStream input = rs.getBinaryStream("audio_data");

                File tempMp3 = File.createTempFile("song_", ".mp3");
                tempMp3.deleteOnExit();

                OutputStream out = new FileOutputStream(tempMp3);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                media = new Media(tempMp3.toURI().toString()); // Assign value to media
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return media;

    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
