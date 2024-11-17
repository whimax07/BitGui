package org.example;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class CellBox extends JPanel {

    private static final String TOP_LEFT_CONNER = "┌";
    private static final String TOP_RIGHT_CONNER = "┐";
    private static final String BOTTOM_LEFT_CONNER = "└";
    private static final String BOTTOM_RIGHT_CONNER = "┘";
    private static final String VERTICAL = "│";
    private static final String HORIZONTAL = "─";



    public CellBox() {
        init();
    }

    private void init() {
        setBackground(new Color(0, 0, 0, 150));
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Dimension cellSize = CellLayout.cellSize(getGraphics(), getFont());

        final int width = (int) (getWidth() / cellSize.getWidth());
        final int height = (int) (getHeight() / cellSize.getHeight());

        if (width <= 0 || height <= 0) return;

        final FontMetrics fontMetrics = g.getFontMetrics();
        final int descent = fontMetrics.getDescent();

        if (width == 1) {
            for (int i = 0; i < height; i++) {
                g.drawString(VERTICAL, 0, (cellSize.height * (i + 1)) - descent);
            }
            return;
        }

        if (height == 1) {
            final String bar = HORIZONTAL.repeat(width);
            g.drawString(bar, 0, cellSize.height - descent);
            return;
        }

        final int rightHandSide = getWidth() - cellSize.width;
        for (int i = 0; i < height; i++) {
            if (i == 0) {
                final String topBar = TOP_LEFT_CONNER + HORIZONTAL.repeat(width - 2) + TOP_RIGHT_CONNER;
                g.drawString(topBar, 0, cellSize.height - descent);
                continue;
            }

            if (i == height - 1) {
                final String bottomBar = BOTTOM_LEFT_CONNER + HORIZONTAL.repeat(width - 2) + BOTTOM_RIGHT_CONNER;
                g.drawString(bottomBar, 0, (cellSize.height * height) - descent);
                continue;
            }

            final int yPos = (cellSize.height * (i + 1)) - descent;
            g.drawString(VERTICAL, 0, yPos);
            g.drawString(VERTICAL, rightHandSide, yPos);
        }
    }

}
