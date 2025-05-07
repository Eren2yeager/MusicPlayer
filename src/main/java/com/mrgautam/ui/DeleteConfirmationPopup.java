package com.mrgautam.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class DeleteConfirmationPopup {

    public static boolean showDeleteConfirmation(Component parent, String songTitle) {
        // Custom message
        JLabel message = new JLabel("Do you really want to delete this song? 😿");
        message.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        // message.setForeground(Color.WHITE);

        // Custom panel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BorderLayout());
        panel.add(message, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Custom buttons
        UIManager.put("OptionPane.background", panel.getBackground());
        UIManager.put("Panel.background", panel.getBackground());
        UIManager.put("Button.background", new Color(50, 50, 50));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));

        // Show confirm dialog
        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Delete Song - " + songTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        return result == JOptionPane.OK_OPTION;
    }
}
