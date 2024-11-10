package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.LinkedHashSet;

@Slf4j
public class CellLayout implements LayoutManager2 {

    private final LinkedHashSet<CompRef> componentSet = new LinkedHashSet<>();
    private int cellWidth;
    private int cellHeight;



    public CellLayout(Dimension cellSize) {
        this.cellWidth = cellSize.width;
        this.cellHeight = cellSize.height;
    }

    public CellLayout(int cellWidth, int cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }



    @Override
    public void addLayoutComponent(String name, Component comp) {
        log.warn(
                "Adding component with default constraints. " +
                "Please use addLayoutComponent(Component comp, Object constraints) instead. " +
                "[Id={}, Component={}]",
                name, comp
        );

        final Constraints constraints = new Constraints();
        constraints.id = name;

        componentSet.add(new CompRef(comp, constraints));
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof Constraints straits) {
            componentSet.add(new CompRef(comp, straits));
        } else if (constraints == null) {
            componentSet.add(new CompRef(comp, new Constraints()));
        } else {
            throw new RuntimeException("Incorrect constraint type. " + constraints.getClass().getName());
        }
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        componentSet.removeIf(compRef -> compRef.component.equals(comp));
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        int maxX = 0;
        int maxY = 0;
        for (CompRef compRef : componentSet) {
            final Constraints constraints = compRef.constraints;
            maxX = Math.max(maxX, constraints.xStart + constraints.width);
            maxY = Math.max(maxY, constraints.yStart + constraints.height);
        }

        final Insets insets = parent.getInsets();
        final int xSize = (cellWidth * maxX) + insets.left + insets.right;
        final int ySize = (cellHeight * maxY) + insets.top + insets.bottom;
        return new Dimension(xSize, ySize);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int maxX = 0;
        int maxY = 0;
        for (CompRef compRef : componentSet) {
            final Constraints constraints = compRef.constraints;
            maxX = Math.max(maxX, constraints.xStart + constraints.width);
            maxY = Math.max(maxY, constraints.yStart + constraints.height);
        }

        final Insets insets = parent.getInsets();
        final int xSize = (cellWidth * maxX) + insets.left + insets.right;
        final int ySize = (cellHeight * maxY) + insets.top + insets.bottom;
        return new Dimension(xSize, ySize);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        int maxX = 0;
        int maxY = 0;
        for (CompRef compRef : componentSet) {
            final Constraints constraints = compRef.constraints;
            maxX = Math.max(maxX, constraints.xStart + constraints.width);
            maxY = Math.max(maxY, constraints.yStart + constraints.height);
        }

        final Insets insets = target.getInsets();
        final int xSize = (cellWidth * maxX) + insets.left + insets.right;
        final int ySize = (cellHeight * maxY) + insets.top + insets.bottom;
        return new Dimension(xSize, ySize);
    }

    @Override
    public void layoutContainer(Container parent) {
        final Insets insets = parent.getInsets();
        for (CompRef compRef : componentSet) {
            final Constraints constraints = compRef.constraints();
            final int startPixelX = (constraints.xStart * cellWidth) + insets.left;
            final int startPixelY = (constraints.yStart * cellHeight) + insets.top;
            final int widthPixels = constraints.width * cellWidth;
            final int heightPixels = constraints.height * cellHeight;

            compRef.component.setBounds(startPixelX, startPixelY, widthPixels, heightPixels);
        }
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {
        // NOTE(Max): We don't currently catch anything.
    }

    public Dimension getCellSize() {
        return new Dimension(cellWidth, cellHeight);
    }

    public void setCellSize(Dimension cellSize) {
        this.cellWidth = cellSize.width;
        this.cellHeight = cellSize.height;
    }



    public static Dimension cellSize(Graphics g, Font monoFont) {
        final FontMetrics fontMetrics = g.getFontMetrics(monoFont);
        return cellSize(fontMetrics);
    }

    public static Dimension cellSize(FontMetrics monoFontMetrics) {
        return new Dimension(
                monoFontMetrics.charWidth('─'),
                monoFontMetrics.getAscent() + monoFontMetrics.getDescent()
        );
    }



    @Data
    @With
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Constraints {
        public Constraints(int xStart, int yStart, int width, int height) {
            this("NO_NAME_GIVEN", xStart, yStart, width, height);
        }

        private String id = "";
        private int xStart = 0;
        private int yStart = 0;
        private int width = 1;
        private int height = 1;
    }

    private record CompRef(Component component, Constraints constraints) { }

}
