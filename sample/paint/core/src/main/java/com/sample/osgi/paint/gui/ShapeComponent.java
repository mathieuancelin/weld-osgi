package com.sample.osgi.paint.gui;

import com.sample.osgi.paint.api.Shape;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class ShapeComponent extends JComponent {

    private Shape shape;

    public ShapeComponent(Shape shape) {
        this.shape = shape;
    }

    public String getShapeId() {
        return shape.getClass().getName();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        shape.draw(g2, new Point(getWidth() / 2, getHeight() / 2));
    }
}
