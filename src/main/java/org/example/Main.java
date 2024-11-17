package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.CellLayout.Constraints;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL;


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

        final ArrayList<JComponent> things = new ArrayList<>();

        things.addAll(addAsicBox(contentPane));
        things.addAll(addCellBoxes(contentPane));
        things.addAll(addPanelWithBorder(contentPane));
        things.addAll(addOverlayingJLabel(contentPane));
        things.addAll(tilePanel(contentPane));

        things.forEach(thing -> thing.setFont(monoSpacedFont));

        final MouseAdapter changeFontSizeListener = createMouseAdaptor(
                contentPane, cellLayout, things, monoSpacedFont
        );
        contentPane.addMouseWheelListener(changeFontSizeListener);

        frame.setVisible(true);
    }

    private static List<JComponent> addAsicBox(Container contentPane) {
        final JLabel topBar =    new JLabel("┌│───┐");
        final JLabel middleBar = new JLabel("││   │");
        final JLabel bottomBar = new JLabel("└│y!───┘");

        topBar.setForeground(Color.WHITE);
        middleBar.setForeground(Color.WHITE);
        bottomBar.setForeground(Color.WHITE);

        contentPane.add(topBar, new Constraints("topBar", 0, 10, 20, 1));
        contentPane.add(middleBar, new Constraints("middleBar", 0, 11, 20, 1));
        contentPane.add(bottomBar, new Constraints("bottomBar", 0, 12, 20, 1));


        final CellLabel wikiExample = new CellLabel(
                """
                ┌─┐ ┌┬┐
                │ │ ├┼┤
                └─┘ └┴┘
                """
        );
        wikiExample.setForeground(Color.WHITE);
        contentPane.add(wikiExample, new Constraints(0, 20, 10, 10));

        return List.of(topBar, middleBar, bottomBar, wikiExample);
    }

    private static List<JComponent> addCellBoxes(Container contentPane) {
        final CellBox cellBoxHoz = new CellBox();
        final CellBox cellBoxVert = new CellBox();
        final CellBox cellBoxRec = new CellBox();

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

        return List.of(cellBoxHoz, cellBoxVert, cellBoxRec);
    }

    private static List<JComponent> addPanelWithBorder(Container contentPane) {
        final JPanel panel = new JPanel();
        panel.setBackground(new Color(0xCB580797, true));
        panel.setBorder(new LineBorder(new Color(0xDF5B15), 4));

        contentPane.add(panel, new Constraints("panelWithBorder", 20, 20, 10, 5));

        return List.of(panel);
    }

    private static List<JComponent> addOverlayingJLabel(Container contentPane) {
        final CellLabel overlayText = new CellLabel("Overlay text");
        overlayText.setForeground(Color.WHITE);
        contentPane.add(overlayText, new Constraints("overlayText", 0, 0, 30, 1));

        return List.of(overlayText);
    }

    private static List<JComponent> tilePanel(Container contentPane) {
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

        return List.of();
    }

    private static MouseAdapter createMouseAdaptor(
            Container contentPane, CellLayout cellLayout, List<JComponent> things, Font monoSpacedFont
    ) {
        return new MouseAdapter() {
            private Font font = monoSpacedFont;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!e.isControlDown()) return;
                if (e.getScrollType() != WHEEL_UNIT_SCROLL) return;

                final int newFontSize = font.getSize() - (int) Math.signum(e.getUnitsToScroll());
                final int boundedSize = Math.max(4, Math.min(2000, newFontSize));
                font = new Font(font.getName(), font.getStyle(), boundedSize);
                final Dimension newCellSize = CellLayout.cellSize(contentPane.getGraphics(), font);

                cellLayout.setCellSize(newCellSize);
                things.forEach(thing -> thing.setFont(font));
                contentPane.repaint();
            }
        };
    }

    private static Font makeFont(int size) {
        return new Font("Courier New", Font.PLAIN, size);
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
