package com.sample.osgi.paint.square;

import com.sample.osgi.paint.api.Shape;
import com.sample.osgi.paint.api.ShapeProvider;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.weld.environment.osgi.api.extension.Publish;

@Publish
@ApplicationScoped
public class SquareShapeProvider implements ShapeProvider {

    @Override
    public Shape getShape() {
        return new Square();
    }

    @Override
    public String getId() {
        return Square.class.getName();
    }

}
