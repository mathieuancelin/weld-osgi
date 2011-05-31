package com.sample.osgi.paint.square;

import com.sample.osgi.paint.api.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Square implements Shape {

    private Icon icon;

    public Square() {
        icon = new ImageIcon(getClass().getResource("square.png"));
    }

    @Override
    public String getName() {
        return "Square";
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void draw(Graphics2D g2, Point p) {
        int x = p.x - 25;
        int y = p.y - 25;
        GradientPaint gradient = new GradientPaint(x, y, Color.BLUE, x + 50, y, Color.WHITE);
        g2.setPaint(gradient);
        g2.fill(new Rectangle2D.Double(x, y, 50, 50));
        BasicStroke wideStroke = new BasicStroke(2.0f);
        g2.setColor(Color.black);
        g2.setStroke(wideStroke);
        g2.draw(new Rectangle2D.Double(x, y, 50, 50));
    }
}
