package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.CellLayout.Constraints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;


@Slf4j
public class Main {

    private static void makeWindow() {
        final JFrame frame = new JFrame("Simple Line Graph");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        final Font defualtFont = new JLabel().getFont();
        final Font monoSpacedFont = new Font("Courier New", Font.PLAIN, defualtFont.getSize() + 12);
        final FontMetrics fontMetrics = frame.getFontMetrics(monoSpacedFont);

        final CellLayout cellLayout = new CellLayout(CellLayout.cellSize(fontMetrics));
        System.out.println("Cell Size: " + cellLayout.getCellSize());

        final Container contentPane = frame.getContentPane();
        contentPane.setBackground(new Color(0x202020));
        contentPane.setLayout(cellLayout);

        addAsicBox(monoSpacedFont, contentPane);

        addOverlayingJLabel(monoSpacedFont, contentPane);

        tilePanel(contentPane);

        frame.setVisible(true);
    }

    private static void addAsicBox(Font monoSpacedFont, Container contentPane) {
        final JLabel topBar =    new JLabel("┌│───┐");
        final JLabel middleBar = new JLabel("││   │");
        final JLabel bottomBar = new JLabel("└│y!───┘");

        topBar.setFont(monoSpacedFont);
        middleBar.setFont(monoSpacedFont);
        bottomBar.setFont(monoSpacedFont);
        topBar.setForeground(Color.WHITE);
        middleBar.setForeground(Color.WHITE);
        bottomBar.setForeground(Color.WHITE);

        contentPane.add(topBar, new Constraints("topBar", 0, 10, 20, 1));
        contentPane.add(middleBar, new Constraints("middleBar", 0, 11, 20, 1));
        contentPane.add(bottomBar, new Constraints("bottomBar", 0, 12, 20, 1));


        final CellLabel wikiExample = new CellLabel(
                monoSpacedFont,
                """
                ┌─┐ ┌┬┐
                │ │ ├┼┤
                └─┘ └┴┘
                """
        );
        wikiExample.setForeground(Color.WHITE);
        contentPane.add(wikiExample, new Constraints(0, 20, 10, 10));
    }

    private static void addOverlayingJLabel(Font monoSpacedFont, Container contentPane) {
        final CellLabel overlayText = new CellLabel(monoSpacedFont, "Overlay text");
        overlayText.setFont(monoSpacedFont);
        overlayText.setForeground(Color.WHITE);
        contentPane.add(overlayText, new Constraints("overlayText", 0, 0, 30, 1));
    }

    private static void tilePanel(Container contentPane) {
        final int tileWidth = 40;
        final int tileHeight = 30;

        final Color lightGrey = new Color(0x808080);
        final Color midGrey = new Color(0x606060);

        for (int i = 0; i < tileWidth; i++) {
            for (int j = 0; j < tileHeight; j++) {
                final JPanel panel = new JPanel();
                final Color colour = ((i + j) % 2 == 0) ? midGrey : lightGrey;
                panel.setBackground(colour);
                contentPane.add(panel, new Constraints(i, j, 1, 1));
            }
        }
        log.error("Done tilling");
    }



    public static void main(String[] args) {
        log.error("Test logging.");
        try {
            SwingUtilities.invokeAndWait(Main::makeWindow);
        } catch (Exception e) {
            log.error("Main erred.", e);
        }

    }

}
