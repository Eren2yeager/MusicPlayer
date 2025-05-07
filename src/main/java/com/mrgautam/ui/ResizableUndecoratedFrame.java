package com.mrgautam.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ResizableUndecoratedFrame extends JFrame {
    private static final int RESIZE_MARGIN = 5;
    private Point clickOffset = null;
    private boolean resizing = false;
    private int cursorType = Cursor.DEFAULT_CURSOR;

    public ResizableUndecoratedFrame() {
        setUndecorated(true);
        setResizable(true);
        enableResize();

        // Optional: Set a default size and background
        setSize(1000, 600);
        getContentPane().setBackground(Color.DARK_GRAY);
        setLocationRelativeTo(null);
    }

    private void enableResize() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                cursorType = getCursorType(e.getPoint());
                setCursor(Cursor.getPredefinedCursor(cursorType));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (cursorType != Cursor.DEFAULT_CURSOR) {
                    resizeWindow(e);
                } else if (clickOffset != null) {
                    // Window drag (move)
                    Point newLocation = e.getLocationOnScreen();
                    setLocation(newLocation.x - clickOffset.x, newLocation.y - clickOffset.y);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (cursorType == Cursor.DEFAULT_CURSOR) {
                    clickOffset = e.getPoint();
                } else {
                    resizing = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resizing = false;
                clickOffset = null;
            }
        });
    }

    private int getCursorType(Point p) {
        int x = p.x;
        int y = p.y;
        int width = getWidth();
        int height = getHeight();

        boolean left = x < RESIZE_MARGIN;
        boolean right = x > width - RESIZE_MARGIN;
        boolean top = y < RESIZE_MARGIN;
        boolean bottom = y > height - RESIZE_MARGIN;

        if (left && top) return Cursor.NW_RESIZE_CURSOR;
        if (left && bottom) return Cursor.SW_RESIZE_CURSOR;
        if (right && top) return Cursor.NE_RESIZE_CURSOR;
        if (right && bottom) return Cursor.SE_RESIZE_CURSOR;
        if (left) return Cursor.W_RESIZE_CURSOR;
        if (right) return Cursor.E_RESIZE_CURSOR;
        if (top) return Cursor.N_RESIZE_CURSOR;
        if (bottom) return Cursor.S_RESIZE_CURSOR;

        return Cursor.DEFAULT_CURSOR;
    }

    private void resizeWindow(MouseEvent e) {
        Point p = e.getPoint();
        Rectangle bounds = getBounds();

        switch (cursorType) {
            case Cursor.NW_RESIZE_CURSOR:
                int dxNW = p.x;
                int dyNW = p.y;
                bounds.x += dxNW;
                bounds.y += dyNW;
                bounds.width -= dxNW;
                bounds.height -= dyNW;
                break;
            case Cursor.NE_RESIZE_CURSOR:
                int dxNE = p.x - bounds.width;
                int dyNE = p.y;
                bounds.y += dyNE;
                bounds.width += dxNE;
                bounds.height -= dyNE;
                break;
            case Cursor.SW_RESIZE_CURSOR:
                int dxSW = p.x;
                int dySW = p.y - bounds.height;
                bounds.x += dxSW;
                bounds.width -= dxSW;
                bounds.height += dySW;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                bounds.width = p.x;
                bounds.height = p.y;
                break;
            case Cursor.W_RESIZE_CURSOR:
                int dxW = p.x;
                bounds.x += dxW;
                bounds.width -= dxW;
                break;
            case Cursor.E_RESIZE_CURSOR:
                bounds.width = p.x;
                break;
            case Cursor.N_RESIZE_CURSOR:
                int dyN = p.y;
                bounds.y += dyN;
                bounds.height -= dyN;
                break;
            case Cursor.S_RESIZE_CURSOR:
                bounds.height = p.y;
                break;
        }

        // Set minimum size
        if (bounds.width < 300) bounds.width = 300;
        if (bounds.height < 300) bounds.height = 300;

        setBounds(bounds);
    }
}
