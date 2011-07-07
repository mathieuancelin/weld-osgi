package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class CDIOSGiAnnotatedField<T> implements AnnotatedField<T> {

    AnnotatedField field;
    Set<Annotation> annotations = new HashSet<Annotation>();
    Filter filter;

    public CDIOSGiAnnotatedField(final AnnotatedField<? super T> field) {
        this.field = field;
        filter = field.getAnnotation(Filter.class);
        if (filter == null) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(Filter.class)) {
                    filter = (Filter) annotation.annotationType().getAnnotation(Filter.class);
                    break;
                }
            }
        }
        filter = FilterGenerator.makeFilter(filter,field.getAnnotations());
        annotations.add(filter);
        //annotations.add(new AnnotationLiteral<OSGiService>() {});
        annotations.add(new OSGiServiceQualifier(field.getJavaMember().getAnnotation(OSGiService.class).value()));
        if(field.getAnnotation(Required.class) != null) {
            annotations.add(new AnnotationLiteral<Required>() {});
        }
        for(Annotation annotation : field.getAnnotations()) {
            if(!annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                annotations.add(annotation);
            }
        }
    }

    @Override
    public Field getJavaMember() {
        return field.getJavaMember();
    }

    @Override
    public boolean isStatic() {
        return field.isStatic();
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        return field.getDeclaringType();
    }

    @Override
    public Type getBaseType() {
        return field.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return field.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) {
                return (T)annotation;
            }
        }
        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if(getAnnotation(annotationType) == null) {
            return false;
        }
        return true;
    }
}
