package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.CellLayout.Constraints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
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
        addCellBoxes(monoSpacedFont, contentPane);
        addPanelWithBorder(contentPane);

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

    private static void addCellBoxes(Font monoSpacedFont, Container contentPane) {
        final CellBox cellBoxHoz = new CellBox(monoSpacedFont);
        final CellBox cellBoxVert = new CellBox(monoSpacedFont);
        final CellBox cellBoxRec = new CellBox(monoSpacedFont);

        cellBoxHoz.setForeground(Color.WHITE);
        cellBoxVert.setForeground(Color.WHITE);
        cellBoxRec.setForeground(Color.WHITE);

        final Constraints hozSize = new Constraints("hozBox", 0.1f, -1f, 0.8f, -1f);
        hozSize.setYStart(1);
        hozSize.setHeight(1);
        contentPane.add(cellBoxHoz, hozSize);

        final Constraints vertSize = new Constraints("vertBox", 0.1f, 0.1f, -1f, 0.6f);
        vertSize.setWidth(1);
        contentPane.add(cellBoxVert, vertSize);

        contentPane.add(cellBoxRec, new Constraints("recBox", 0.2f, 0.2f, 0.4f, 0.4f));
    }

    private static void addPanelWithBorder(Container contentPane) {
        final JPanel panel = new JPanel();
        panel.setBackground(new Color(0xCB580797, true));
        panel.setBorder(new LineBorder(new Color(0xDF5B15), 4));

        contentPane.add(panel, new Constraints("panelWithBorder", 20, 20, 10, 5));
    }

    private static void addOverlayingJLabel(Font monoSpacedFont, Container contentPane) {
        final CellLabel overlayText = new CellLabel(monoSpacedFont, "Overlay text");
        overlayText.setFont(monoSpacedFont);
        overlayText.setForeground(Color.WHITE);
        contentPane.add(overlayText, new Constraints("overlayText", 0, 0, 30, 1));
    }

    private static void tilePanel(Container contentPane) {
        final int tileWidth = 80;
        final int tileHeight = 25;

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
