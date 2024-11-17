package org.example;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class CellLabel extends JPanel {

    private String[] text = new String[0];



    public CellLabel() {
        init();
    }

    public CellLabel(String text) {
        this.text = text.split("\n");
        init();
    }

    private void init() {
        setBackground(new Color(0, 0, 0, 0));
    }



    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text.split("\n");
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final FontMetrics fontMetrics = g.getFontMetrics();
        final Dimension cellSize = CellLayout.cellSize(fontMetrics);
        final int descent = fontMetrics.getDescent();

        for (int i = 0; i < text.length; i++) {
            final int height = (cellSize.height * (i + 1)) - descent;
            g.drawString(text[i], 0, height);
        }
    }

}
