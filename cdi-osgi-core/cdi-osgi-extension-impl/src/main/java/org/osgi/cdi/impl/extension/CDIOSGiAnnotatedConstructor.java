package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.OSGiService;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CDIOSGiAnnotatedConstructor<T> implements AnnotatedConstructor<T> {

    AnnotatedConstructor constructor;
    List<AnnotatedParameter<T>> parameters = new ArrayList<AnnotatedParameter<T>>();

    public CDIOSGiAnnotatedConstructor(AnnotatedConstructor<T> constructor) {
        this.constructor = constructor;
        for(AnnotatedParameter parameter : constructor.getParameters()) {
            if(parameter.isAnnotationPresent(OSGiService.class)) {
                parameters.add(new CDIOSGIAnnotatedParameter(parameter));
            } else {
                parameters.add(parameter);
            }
        }
    }

    @Override
    public Constructor<T> getJavaMember() {
        return constructor.getJavaMember();
    }

    @Override
    public boolean isStatic() {
        return constructor.isStatic();
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        return constructor.getDeclaringType();
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return parameters;
    }

    @Override
    public Type getBaseType() {
        return constructor.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return constructor.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return constructor.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return constructor.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return constructor.isAnnotationPresent(annotationType);
    }
}
