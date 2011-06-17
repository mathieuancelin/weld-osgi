package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.OSGiService;

import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class CDIOSGiAnnotatedType<T> implements AnnotatedType<T> {

    AnnotatedType<T> annotatedType;
    Set<AnnotatedConstructor<T>> constructors = new HashSet<AnnotatedConstructor<T>>();
    Set<AnnotatedMethod<? super T>> methods = new HashSet<AnnotatedMethod<? super T>>();
    Set<AnnotatedField<? super T>> fields = new HashSet<AnnotatedField<? super T>>();


    public CDIOSGiAnnotatedType(AnnotatedType<T> annotatedType) {
        this.annotatedType = annotatedType;
        process();
    }

    private void process() {
        for(AnnotatedConstructor<T> constructor : annotatedType.getConstructors()) {
            if(isCDIOSGiConstructor(constructor)) {
                constructors.add(new CDIOSGiAnnotatedConstructor<T>(constructor));
            } else {
                constructors.add(constructor);
            }
        }
        for(AnnotatedMethod<? super T> method : annotatedType.getMethods()) {
            if(isCDIOSGiMethod(method)) {
                methods.add(new CDIOSGiAnnotatedMethod<T>(method));
            } else {
                methods.add(method);
            }
        }
        for(AnnotatedField<? super T> field : annotatedType.getFields()) {
            if(isCDIOSGiField(field)) {
                fields.add(new CDIOSGiAnnotatedField<T>(field));
            } else {
                fields.add(field);
            }
        }
    }

    @Override
    public Class getJavaClass() {
        return annotatedType.getJavaClass();
    }

    @Override
    public Set<AnnotatedConstructor<T>> getConstructors() {
        return constructors;
    }

    @Override
    public Set<AnnotatedMethod<? super T>> getMethods() {
        return methods;
    }

    @Override
    public Set<AnnotatedField<? super T>> getFields() {
        return fields;
    }

    @Override
    public Type getBaseType() {
        return annotatedType.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return annotatedType.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return annotationType.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotatedType.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return annotatedType.isAnnotationPresent(annotationType);
    }

    private boolean isCDIOSGiField(AnnotatedField<? super T> field) {
        if(field.isAnnotationPresent(Inject.class) && field.isAnnotationPresent(OSGiService.class)) {
            return true;
        }
        return false;
    }

    private boolean isCDIOSGiMethod(AnnotatedMethod<? super T> method) {
        if(method.isAnnotationPresent(Inject.class)) {
            for(AnnotatedParameter parameter : method.getParameters()) {
                if(parameter.isAnnotationPresent(OSGiService.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCDIOSGiConstructor(AnnotatedConstructor<T> constructor) {
        if(constructor.isAnnotationPresent(Inject.class)) {
            for(AnnotatedParameter parameter : constructor.getParameters()) {
                if(parameter.isAnnotationPresent(OSGiService.class)) {
                    return true;
                }
            }
        }
        return false;
    }
}
