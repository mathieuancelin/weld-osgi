package com.sample.osgi.paint.api;

import java.util.List;

public interface ShapeContainer extends Shape {

    List<Shape> getShapes();

    void addShape(Shape shape);
    
    void clearShapes();
}
