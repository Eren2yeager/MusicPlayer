package com.mrgautam.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class RotatingWheelPanel extends JPanel {

    Timer timer;
    public double angle = 0;
    Graphics2D g2d;
    double scale = 0;
    int size;
    private BufferedImage discImage;
    private static final int WHEEL_SIZE = 100; // Size of the rotating wheel
    private static final int MARGIN = 20; // Margin from the edges
    private Color[] gradientColors = {
        new Color(25, 25, 25),
        new Color(35, 35, 35),
        new Color(45, 45, 45)
    };
    public JLabel addImageLabel = new JLabel("Add Song Image");

    // SongService service = new SongService();
    // Song song = service.getSongById(2); // Assuming you have a method to get the song
    public RotatingWheelPanel() {
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(500, 500));
        setBackground(Color.DARK_GRAY); // Transparent background
        addImageLabel.setVisible(false);

        timer = new Timer(20, e -> {
            angle += 0.03;
            if (scale <= 1.0) { // Reduced max scale
                scale += 0.03;
            }
            repaint();
        });
    }

    public void startRotationAtNewSong() {
        addImageLabel.setVisible(true);
        scale = 0;
        timer.start();
    }

    public void startRotation() {
        timer.start();
    }

    public void stopRotation() {
        timer.stop();
    }

    public void setImage(BufferedImage image) {
        this.discImage = image;
    }

    public static BufferedImage iconToBufferedImage(ImageIcon icon) {
        Image img = icon.getImage();

        // Create a buffered image with transparency
        BufferedImage buffered = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return buffered;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw the background image first
        if (discImage != null) {
            addImageLabel.setVisible(false);

            size = WHEEL_SIZE; // Fixed size for the wheel
            // Calculate the maximum size that maintains aspect ratio
            double imageAspect = (double) discImage.getWidth() / discImage.getHeight();
            double panelAspect = (double) getWidth() / getHeight();

            int drawWidth, drawHeight;
            int x, y;

            // Calculate dimensions to fill the panel while maintaining aspect ratio
            if (imageAspect > panelAspect) {
                drawHeight = getHeight();
                drawWidth = (int) (drawHeight * imageAspect);
                x = (getWidth() - drawWidth) / 2;
                y = 0;
            } else {
                drawWidth = getWidth();
                drawHeight = (int) (drawWidth / imageAspect);
                x = 0;
                y = (getHeight() - drawHeight) / 2;
            }

            // Draw the image centered
            g2d.drawImage(discImage, x, y, drawWidth, drawHeight, null);

            // Calculate position for bottom-right corner
            int wheelX = getWidth() - WHEEL_SIZE - MARGIN;
            int wheelY = getHeight() - WHEEL_SIZE - MARGIN;

            // Draw the rotating wheel
            g2d.translate(wheelX + WHEEL_SIZE / 2, wheelY + WHEEL_SIZE / 2);
            g2d.scale(scale, scale);
            g2d.rotate(angle);

            // Create clipping region for the wheel
            Ellipse2D.Double clip = new Ellipse2D.Double(-size / 2 + 3, -size / 2 + 3, size - 6, size - 6);
            g2d.setClip(clip);

            // Draw the wheel's center image
            if (discImage != null) {
                int imgSize = size - 6;
                g2d.drawImage(discImage, -imgSize / 2, -imgSize / 2, imgSize, imgSize, null);
            }

            // Reset clip before drawing the border
            g2d.setClip(null);

            // Draw outer ring with subtle gradient
            g2d.setStroke(new BasicStroke(6)); // Reduced stroke width
            for (int i = 0; i < gradientColors.length; i++) {
                g2d.setColor(gradientColors[i]);
                g2d.drawOval(-size / 2 + i * 2, -size / 2 + i * 2, size - i * 4, size - i * 4);
            }

            // Draw center hole with metallic effect
            g2d.setColor(new Color(15, 15, 15));
            g2d.fillOval(-size / 4, -size / 4, size / 2, size / 2);

            // Draw center ring
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(60, 60, 60));
            g2d.drawOval(-size / 4, -size / 4, size / 2, size / 2);

            // Draw inner circle
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillOval(-size / 6, -size / 6, size / 3, size / 3);

            g2d.dispose();
        } else {
            size = Math.min(getWidth(), getHeight()) / 2;

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g2d.translate(centerX, centerY);
            g2d.scale(scale, scale); // Scale down the image to fit the panel
            g2d.rotate(angle);

            // int imgW = discImage.getWidth() / 2;
            // int imgH = discImage.getHeight() / 2;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(-size / 2, -size / 2, size, size);

            g2d.setStroke(new BasicStroke(10));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawOval(-size / 2, -size / 2, size, size);

            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(-size / 4, -size / 4, size / 2, size / 2);

            g2d.setStroke(new BasicStroke(60));
            g2d.setColor(Color.darkGray);
            g2d.drawOval(-size / 4, -size / 4, size / 2, size / 2);

            // g2d.setColor(Color.GREEN);
            // g2d.fillOval(-2*size / 3, -2*size / 3, 2*size / 3, 2*size / 3);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(-size / 3, -size / 3, size / 3, size / 3);

            g2d.setStroke(new BasicStroke(20));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawOval(-size / 3, -size / 3, size / 3, size / 3);

            // g2d.drawImage(discImage, -size / 2, -size / 2, null);
            //  g2d.drawImage(discImage.getScaledInstance(-imgW, -imgH, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();

            setLayout(new java.awt.BorderLayout());
            addImageLabel.setPreferredSize(new java.awt.Dimension(100, 20));
            addImageLabel.setFont(addImageLabel.getFont().deriveFont(12f));
            addImageLabel.setForeground(Color.LIGHT_GRAY);
            addImageLabel.setBackground(Color.DARK_GRAY);
            addImageLabel.setOpaque(true);
            add(addImageLabel, java.awt.BorderLayout.SOUTH);
            addImageLabel.setHorizontalAlignment(JLabel.CENTER);

        }
    }
}
