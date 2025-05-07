package com.mrgautam.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomTitleBar extends JPanel {

    private boolean isFullScreen = false;
    private Rectangle normalBounds;
    private final JFrame parentFrame;
    private final JButton fullscreenButton;

    public CustomTitleBar(JFrame frame, String title) {
        this.parentFrame = frame;
        this.normalBounds = frame.getBounds();

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        ImageIcon icon = new ImageIcon("Q:\\SEMESTER 4\\java\\swingMusicPlayer\\src\\main\\resources\\icons\\converted_icon.png"); // Adjust path as needed
        Image scaledIcon = icon.getImage().getScaledInstance(20, 16, Image.SCALE_SMOOTH); // Resize
        JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));

        JLabel titleText = new JLabel(title);
        titleText.setForeground(Color.WHITE);
        titleText.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel titleInfo = new JPanel();
        titleInfo.setOpaque(false);
        titleInfo.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titleInfo.add(iconLabel);
        titleInfo.add(titleText);

        // === Buttons ===
        JButton minimizeButton = new JButton("—");
        fullscreenButton = new JButton("⬜");
        JButton closeButton = new JButton("✖");

        for (JButton btn : new JButton[]{minimizeButton, fullscreenButton, closeButton}) {
            btn.setFocusPainted(false);
            btn.setBorder(null);
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(40, 30));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(minimizeButton);
        buttonPanel.add(fullscreenButton);
        buttonPanel.add(closeButton);

        add(titleInfo, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.EAST);

        // === Button Actions ===
        minimizeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setState(Frame.ICONIFIED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeButton.setBackground(new Color(45, 45, 45));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizeButton.setBackground(Color.BLACK);
            }

        });

        fullscreenButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleFullscreen();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                fullscreenButton.setBackground(new Color(45, 45, 45));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fullscreenButton.setBackground(Color.BLACK);
            }

        });

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 System.exit(0);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(Color.BLACK);
            }

        });


        // === Drag Functionality ===
        Point clickPoint = new Point();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                clickPoint.x = e.getX();
                clickPoint.y = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!isFullScreen) {
                    int x = e.getXOnScreen() - clickPoint.x;
                    int y = e.getYOnScreen() - clickPoint.y;
                    frame.setLocation(x, y);
                }
            }
        });
    }

    private void toggleFullscreen() {
        if (!isFullScreen) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            parentFrame.dispose();
            parentFrame.setUndecorated(true);
            gd.setFullScreenWindow(parentFrame);
            isFullScreen = true;
            fullscreenButton.setText("🗗");
        } else {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            gd.setFullScreenWindow(null);
            parentFrame.dispose();
            parentFrame.setUndecorated(true);
            parentFrame.setBounds(normalBounds);
            parentFrame.setVisible(true);
            isFullScreen = false;
            fullscreenButton.setText("⬜");
        }
    }
}
