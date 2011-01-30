package com.sample.osgi.paint.api;

import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.Icon;

public interface Shape {

    String getName();

    Icon getIcon();

    void draw(Graphics2D g2, Point p);
}
