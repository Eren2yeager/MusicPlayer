package com.mrgautam.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.mrgautam.model.Song;

public class EditSongDetailsPanel extends JPanel {

    public JButton addImageButton = new JButton(" ➕ Add Image");
    public JPanel panel = new JPanel();
    public int inputResult;

    public JTextField titleField = new JTextField(30);
    public JTextField artistField = new JTextField(30);
    public JTextField songImageFeild = new JTextField(30);

    public Font font = new Font("Segoe UI Symbol", Font.PLAIN, 14);
    public Color bgColor = new Color(30, 30, 30, (int) (0.9f * 255)); // Semi-transparent black
    public Color fgColor = Color.WHITE;

    public EditSongDetailsPanel(Song selectedSong) {

        titleField.setBackground(bgColor);
        titleField.setForeground(fgColor);
        titleField.setFont(font);
        // titleField.setCaretColor(fgColor);
        titleField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true), // `true` makes it rounded
                new EmptyBorder(5, 5, 5, 5) // Padding inside the border
        ));

        artistField.setBackground(bgColor);
        artistField.setForeground(fgColor);
        artistField.setFont(font);
        // artistField.setCaretColor(fgColor);
        artistField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true), // `true` makes it rounded
                new EmptyBorder(5, 5, 5, 5) // Padding inside the border
        ));

        songImageFeild.setBackground(bgColor);
        songImageFeild.setForeground(fgColor);
        songImageFeild.setFont(font);
        // artistField.setCaretColor(fgColor);
        songImageFeild.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true), // `true` makes it rounded
                new EmptyBorder(5, 5, 5, 5) // Padding inside the border
        ));

        JLabel titleLabel = new JLabel("Title:");
        JLabel artistLabel = new JLabel("Artist:");
        JLabel addImagelabel = new JLabel("Add Song Image (OPTIONAL):");

        titleLabel.setForeground(fgColor);
        artistLabel.setForeground(fgColor);
        addImagelabel.setForeground(fgColor);

        addImageButton.setForeground(bgColor);
        addImageButton.setBackground(Color.ORANGE);
        addImageButton.setFocusPainted(false);
        addImageButton.setFont(font);
        addImageButton.setPreferredSize(new Dimension(100, 30));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.black);
        panel.add(Box.createVerticalStrut(10));
        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(artistLabel);
        panel.add(artistField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addImagelabel);
        panel.add(songImageFeild);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addImageButton);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.background", bgColor);
        UIManager.put("OptionPane.messageForeground", fgColor);
        UIManager.put("Button.background", new Color(50, 50, 50));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", font);

        if (selectedSong != null) {
            titleField.setText(selectedSong.getTitle());
            artistField.setText(selectedSong.getArtist());
            if (!(selectedSong.getSongImage(selectedSong.getId()) == null)) {
                addImageButton.setText("Change Image");
            }

            addImageButton.addActionListener(ev -> {
                FileDialog fileDialog1 = new FileDialog((java.awt.Frame) null, "Select Image File", FileDialog.LOAD);
                fileDialog1.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));
                fileDialog1.setVisible(true);
                if (fileDialog1.getFile() != null) {
                    songImageFeild.setText(new File(fileDialog1.getDirectory(), fileDialog1.getFile()).getAbsolutePath());
                }
            });


            inputResult = JOptionPane.showConfirmDialog(
                    null, panel, "Add Song Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

        }
    }
}