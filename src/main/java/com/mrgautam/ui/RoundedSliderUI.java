package com.mrgautam.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class RoundedSliderUI extends BasicSliderUI {

    public RoundedSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Rectangle trackBounds = trackRect;

        int trackHeight = 8;
        int arc = 5;

        int x = trackBounds.x;
        int y = trackBounds.y + (trackBounds.height - trackHeight) / 2;
        int width = trackBounds.width;

        int filledWidth = xPositionForValue(slider.getValue()) - x;

        g2.setColor(Color.GRAY);
        g2.fillRoundRect(x, y, width, trackHeight, arc, arc);

        g2.setColor(Color.GREEN.darker());
        g2.fillRoundRect(x, y, filledWidth, trackHeight, arc, arc);

        g2.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Custom thumb size (centered in rect)
        int diameter = 10;  // Size of thumb
        int x = thumbRect.x + (thumbRect.width - diameter) / 2;
        int y = thumbRect.y + (thumbRect.height - diameter) / 2;

        g2.setColor(Color.WHITE.brighter());
        g2.fillOval(x, y, diameter, diameter);

        g2.dispose();
    }

    @Override
    protected Dimension getThumbSize() {
        // Tell Swing how big the thumb should be
        return new Dimension(24, 24); // width x height
    }
}
