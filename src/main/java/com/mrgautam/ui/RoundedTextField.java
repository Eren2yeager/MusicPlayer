package com.mrgautam.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class RoundedTextField extends JTextField {
    private Shape shape;

    public RoundedTextField(int size) {
        super(size);
        setOpaque(false); // Make background transparent
        setForeground(Color.WHITE);
        setBackground(new Color(30, 30, 30)); // Dark background like Spotify
        setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 40)); // Padding (left, top, right, bottom)
        setBorder(BorderFactory.createLineBorder(Color.RED,2)); // No border
        setCaretColor(Color.WHITE); // White typing cursor
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // pill-shaped

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Optional: no border
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        }
        return shape.contains(x, y);
    }
}
