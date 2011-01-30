package com.sample.osgi.paint.triangle;

import com.sample.osgi.paint.api.Shape;
import com.sample.osgi.paint.api.ShapeProvider;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.weld.environment.osgi.api.extension.Publish;

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
