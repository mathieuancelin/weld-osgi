package com.sample.osgi.paint.triangle;

import com.sample.osgi.paint.api.Shape;
import com.sample.osgi.paint.api.ShapeProvider;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.context.ApplicationScoped;

@Publish
@ApplicationScoped
public class TriangleShapeProvider implements ShapeProvider {

    @Override
    public Shape getShape() {
        return new Triangle();
    }

    @Override
    public String getId() {
        return Triangle.class.getName();
    }

}
