package com.sample.osgi.paint.circle;

import com.sample.osgi.paint.api.Shape;
import com.sample.osgi.paint.api.ShapeProvider;
import javax.enterprise.context.ApplicationScoped;

@CircleShape
@ApplicationScoped
public class CircleShapeProvider implements ShapeProvider {

    @Override
    public Shape getShape() {
        return new Circle();
    }

    @Override
    public String getId() {
        return Circle.class.getName();
    }
}
