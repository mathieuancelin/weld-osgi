package com.sample.osgi.paint.triangle;

import com.sample.osgi.paint.api.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Triangle implements Shape {

    private Icon icon;

    public Triangle() {
        icon = new ImageIcon(getClass().getResource("triangle.png"));
    }

    @Override
    public String getName() {
        return "Triangle";
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void draw(Graphics2D g2, Point p) {
        int x = p.x - 25;
        int y = p.y - 25;
        GradientPaint gradient = new GradientPaint(x, y, Color.GREEN, x + 50, y, Color.WHITE);
        g2.setPaint(gradient);
        int[] xcoords = {x + 25, x, x + 50};
        int[] ycoords = {y, y + 50, y + 50};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xcoords.length);
        polygon.moveTo(x + 25, y);
        for (int i = 0; i < xcoords.length; i++) {
            polygon.lineTo(xcoords[i], ycoords[i]);
        }
        polygon.closePath();
        g2.fill(polygon);
        BasicStroke wideStroke = new BasicStroke(2.0f);
        g2.setColor(Color.black);
        g2.setStroke(wideStroke);
        g2.draw(polygon);
    }
}
