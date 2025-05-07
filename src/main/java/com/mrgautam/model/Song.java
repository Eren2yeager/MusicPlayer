package com.mrgautam.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;

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

    // public Boolean updateTitle(String newTitle){
    //     try {
    //         Connection conn = DBManager.getConnection();
    //         PreparedStatement ps = conn.prepareStatement("UPDATE songs SET title = ? WHERE id = ?");
    //         ps.setString(1, newTitle);
    //         ps.setInt(2,getId());
    //     } catch (Exception e) {
    //     }
    //     return  false;
    // }
    public Boolean updateTitle(String newTitle) {
        try {
            String query = "UPDATE songs SET title = ? WHERE id = ?";

            Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1,newTitle);
            stmt.setInt(2,getId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Title Update succesful for : " + getId());
                return true;
            } else {
                System.out.println("No song found with ID: " + getId());
                return false;

            }
        } catch (Exception ex) {
            ex.getMessage();
            return false;

        }

    }

    public Boolean updateArtist(String newArtist) {
        try {
            String query = "UPDATE songs SET artist = ? WHERE id = ?";

            Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1,newArtist);
            stmt.setInt(2,getId());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Artist Update succesful for : " + getId());
                return true;
            } else {
                System.out.println("No song found with ID: " + getId());
                return false;

            }
        } catch (Exception ex) {
            ex.getMessage();
            return false;
        }
    }

    public Boolean updateSongImage(String imagePath) {
        try {
            addSongImage(imagePath);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            return false;
        }

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

    public void addSongImage(String imagePath) {
        String query = "UPDATE songs SET song_img = ? WHERE id = ?";

        try (Connection conn = DBManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); FileInputStream fis = new FileInputStream(new File(imagePath))) {

            stmt.setBinaryStream(1, fis, (int) new File(imagePath).length());
            stmt.setInt(2, getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Image inserted successfully for song ID: " + getId());
            } else {
                System.out.println("No song found with ID: " + getId());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public ImageIcon getSongImage(int id) {
        ImageIcon imageIcon = null;

        String query = "SELECT song_img FROM songs WHERE id = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Get the binary stream from DB
                InputStream in = rs.getBinaryStream("song_img");

                // Read image from stream
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] temp = new byte[1024];
                int read;
                while ((read = in.read(temp)) != -1) {
                    buffer.write(temp, 0, read);
                }

                byte[] imageBytes = buffer.toByteArray();
                imageIcon = new ImageIcon(imageBytes);

            } else {
                System.out.println("No image found in database!");
            }

        } catch (Exception e) {
            e.getMessage();
        }
        return imageIcon;
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
