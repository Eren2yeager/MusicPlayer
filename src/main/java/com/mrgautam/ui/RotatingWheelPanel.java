package com.mrgautam.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.Timer;

// public class RotatingWheelPanel extends JPanel {
//     private BufferedImage discImage;
//     private double angle = 0;
//     private Timer timer;
//     public RotatingWheelPanel() {
//         setOpaque(false);
//         setPreferredSize(new Dimension(300, 300)); // Perfect size as you said
//         try {
//             discImage = ImageIO.read(new File("A:\\SEMESTER 4\\java\\swingMusicPlayer\\src\\main\\resources\\icons\\black-disco-cd-music-icon-png-6.png")); // Use your image path
//         } catch (IOException e) {
//             e.printStackTrace();
//             discImage = null; // fallback if image isn't found
//         }
//         timer = new Timer(20, e -> {
//             angle += 0.03;
//             repaint();
//         });
//         startRotation();
//     }
//     public void startRotation() {
//         if (!timer.isRunning()) {
//             timer.start();
//         }
//     }
//     public void stopRotation() {
//         if (timer.isRunning()) {
//             timer.stop();
//         }
//     }
//     @Override
//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         if (discImage == null) return;
//         Graphics2D g2d = (Graphics2D) g.create();
//         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, true);
//         int centerX = getWidth() / 2;
//         int centerY = getHeight() / 2;
//         int size = Math.min(getWidth(), getHeight()) - 40; // To keep margin
//         int imgSize = Math.min(size, discImage.getWidth()); // Maintain original size
//         g2d.translate(centerX, centerY);
//         g2d.rotate(angle);
//         g2d.drawImage(discImage, -imgSize / 2, -imgSize / 2, imgSize, imgSize, null);
//         g2d.dispose();
//     }
// }
// class RotatingWheelPanel extends JPanel {
//     private BufferedImage discImage;
//     private double angle = 0;
//     private Timer timer;
//     public RotatingWheelPanel() {
//         try {
//             discImage = ImageIO.read(new File("A:\\SEMESTER 4\\java\\swingMusicPlayer\\src\\main\\resources\\icons\\black-disco-cd-music-icon-png-6.png")); // Path to your disc image
//             setPreferredSize(new Dimension(100, 100)); // Set preferred size for the panel
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         timer = new Timer(30, e -> {
//             angle += 0.05; // Speed of rotation
//             repaint();
//         });
//     }
//     public void startRotation() {
//         timer.start();
//     }
//     public void stopRotation() {
//         timer.stop();
//     }
//     @Override
//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         if (discImage == null) return;
//         Graphics2D g2d = (Graphics2D) g.create();
//         int x = getWidth() / 2;
//         int y = getHeight() / 2;
//         g2d.translate(x, y);
//         g2d.rotate(angle);
//         int imgW = discImage.getWidth() / 2;
//         int imgH = discImage.getHeight() / 2;
//         g2d.drawImage(discImage, -imgW, -imgH, null);
//         g2d.dispose();
//     }
// }
public class RotatingWheelPanel extends JPanel {
    // JPanel diskPanel;
    Timer timer;
    public double angle = 0;
    // private BufferedImage discImage;
    double scale=0; // Scale factor for the image

    public RotatingWheelPanel() {
        setOpaque(false); // Transparent background
        setPreferredSize(new java.awt.Dimension(500, 500)); // Set preferred size for the panel
        // Removed invalid method call
        // startRotation();

        // diskPanel = new JPanel(new BorderLayout());

        // try {
        //     // discImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
            
        //     // discImage = ImageIO.read(new File("A:\\SEMESTER 4\\java\\swingMusicPlayer\\src\\main\\resources\\icons\\black-disco-cd-music-icon-png-6.png"));
        //     // JLabel imageLabel = new JLabel(new javax.swing.ImageIcon(discImage));
        //     // diskPanel.add(imageLabel, BorderLayout.CENTER);
        //     // diskPanel.setPreferredSize(new Dimension(50, 50)); // Make the panel transparent
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        timer = new Timer(20, e -> {
            angle += 0.03;
            if(scale <=1.5){
                scale += 0.03;
            }
            // if(scale > 2){
            //     scale -= 0.01;
            // }
            repaint();
        });
    }

    public void startRotationAtNewSong(){
        scale = 0; // Reset the scale to 0 for a new song
        timer.start();
    }

    public void startRotation() {
        timer.start();
    }

    public void stopRotation() {
        timer.stop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // if (discImage == null) {
        //     return;
        // }
        int size = Math.min(getWidth(), getHeight()) / 2;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g2d.translate(centerX, centerY);
        g2d.scale(scale, scale); // Scale down the image to fit the panel
        g2d.rotate(angle);

        // int imgW = discImage.getWidth() / 2;
        // int imgH = discImage.getHeight() / 2;
  

        
        
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(-size / 2, -size / 2, size, size);
        
        // g2d.setStroke(new BasicStroke(7));
        // g2d.setColor(Color.GREEN.darker());
        // g2d.drawOval(-size / 2, -size / 2, size, size);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval(-size / 4, -size / 4, size / 2, size / 2);

       g2d.setStroke(new BasicStroke(7));
        g2d.setColor(Color.GREEN.darker());
        g2d.drawOval(-size / 4, -size / 4, size / 2, size / 2);

        // g2d.setColor(Color.GREEN);
        // g2d.fillOval(-2*size / 3, -2*size / 3, 2*size / 3, 2*size / 3);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval(-size / 3, -size / 3, size / 3, size / 3);
        
        g2d.setStroke(new BasicStroke(7));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawOval(-size / 3, -size / 3, size / 3, size / 3);

        // g2d.drawImage(discImage, -size / 2, -size / 2, null);
        //  g2d.drawImage(discImage.getScaledInstance(-imgW, -imgH, java.awt.Image.SCALE_SMOOTH), 0, 0, null);


        g2d.dispose();
    }
}
