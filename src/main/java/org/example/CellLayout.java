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
        doTheThing(parent);
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



    private void doTheThing(Container parent) {
        final Insets insets = parent.getInsets();
        final Dimension size = parent.getSize();
        final int widthCells = (size.width - (insets.left + insets.right)) / cellWidth;
        final int heightCells = (size.height - (insets.top + insets.bottom)) / cellHeight;

        for (CompRef compRef : componentSet) {
            final Constraints constraints = compRef.constraints();
            if (!validate(constraints)) continue;

            final Rec sizeInCells = size(widthCells, heightCells, constraints);

            final int startPixelX = (sizeInCells.x * cellWidth) + insets.left;
            final int startPixelY = (sizeInCells.y * cellHeight) + insets.top;
            final int widthPixels = sizeInCells.width * cellWidth;
            final int heightPixels = sizeInCells.height * cellHeight;

            compRef.component.setBounds(startPixelX, startPixelY, widthPixels, heightPixels);
        }
    }

    private boolean validate(Constraints usage) {
        if (usage.xStart == -1 && usage.xOffset == -1) {
            log.error("Missing x start component of. {}", usage);
            return false;
        }

        if (usage.yStart == -1 && usage.yOffset == -1) {
            log.error("Missing y start component of. {}", usage);
            return false;
        }

        if (usage.width == -1 && usage.xRatio == -1) {
            log.error("Missing width component of. {}", usage);
            return false;
        }

        if (usage.height == -1 && usage.yRatio == -1) {
            log.error("Missing height component of. {}", usage);
            return false;
        }

        return true;
    }

    private Rec size(int width, int height, Constraints usage) {
        final int xStart = (usage.xStart != -1) ? usage.xStart : (int) (width * usage.xOffset);
        final int yStart = (usage.yStart != -1) ? usage.yStart : (int) (height * usage.yOffset);
        final int width_ = (usage.width != -1) ? usage.width : (int) (width * usage.xRatio);
        final int height_ = (usage.height != -1) ? usage.height : (int) (height * usage.yRatio);
        return new Rec(xStart, yStart, width_, height_);
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
        public Constraints(String id, int xStart, int yStart, int width, int height) {
            this(id, xStart, yStart, -1, -1, width, height, -1, -1);
        }

        public Constraints(String id, int xStart, int yStart, float xRatio, float yRatio) {
            this(id, xStart, yStart, -1, -1, -1, -1, xRatio, yRatio);
        }

        public Constraints(String id, float xOffset, float yOffset, int width, int height) {
            this(id, -1, -1, xOffset, yOffset, width, height, -1, -1);
        }

        public Constraints(String id, float xOffset, float yOffset, float xRatio, float yRatio) {
            this(id, -1, -1, xOffset, yOffset, -1, -1, xRatio, yRatio);
        }

        public Constraints(int xStart, int yStart, int width, int height) {
            this("NO_NAME_GIVEN", xStart, yStart, -1, -1, width, height, -1, -1);
        }

        public Constraints(int xStart, int yStart, float xRatio, float yRatio) {
            this("NO_NAME_GIVEN", xStart, yStart, -1, -1, -1, -1, xRatio, yRatio);
        }

        public Constraints(float xOffset, float yOffset, int width, int height) {
            this("NO_NAME_GIVEN", -1, -1, xOffset, yOffset, width, height, -1, -1);
        }

        public Constraints(float xOffset, float yOffset, float xRatio, float yRatio) {
            this("NO_NAME_GIVEN", -1, -1, xOffset, yOffset, -1, -1, xRatio, yRatio);
        }

        private String id = "";
        private int xStart = 0;
        private int yStart = 0;
        private float xOffset = -1;
        private float yOffset = -1;
        private int width = 1;
        private int height = 1;
        private float xRatio = -1;
        private float yRatio = -1;
    }

    private record CompRef(Component component, Constraints constraints) { }

    private record Rec(int x, int y, int width, int height) { }

}
