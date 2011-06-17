package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.OSGiService;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CDIOSGiAnnotatedMethod<T> implements AnnotatedMethod<T> {

    AnnotatedMethod method;
    List<AnnotatedParameter<T>> parameters = new ArrayList<AnnotatedParameter<T>>();

    public CDIOSGiAnnotatedMethod(AnnotatedMethod<? super T> method) {
        this.method = method;
        for(AnnotatedParameter parameter : method.getParameters()) {
            if(parameter.isAnnotationPresent(OSGiService.class)) {
                parameters.add(new CDIOSGIAnnotatedParameter(parameter));
            } else {
                parameters.add(parameter);
            }
        }
    }

    @Override
    public Method getJavaMember() {
        return method.getJavaMember();
    }

    @Override
    public boolean isStatic() {
        return method.isStatic();
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        return method.getDeclaringType();
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return parameters;
    }

    @Override
    public Type getBaseType() {
        return method.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return method.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return method.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return method.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return method.isAnnotationPresent(annotationType);
    }
}
