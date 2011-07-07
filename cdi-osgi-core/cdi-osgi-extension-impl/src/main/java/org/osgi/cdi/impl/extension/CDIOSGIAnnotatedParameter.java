package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class CDIOSGIAnnotatedParameter<T> implements AnnotatedParameter<T> {

    AnnotatedParameter parameter;
    Set<Annotation> annotations = new HashSet<Annotation>();
    Filter filter;

    public CDIOSGIAnnotatedParameter(AnnotatedParameter parameter) {
        this.parameter = parameter;
        filter = parameter.getAnnotation(Filter.class);
        if (filter == null) {
            for (Annotation annotation : parameter.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(Filter.class)) {
                    filter = (Filter) annotation.annotationType().getAnnotation(Filter.class);
                    break;
                }
            }
        }
        filter = FilterGenerator.makeFilter(filter,parameter.getAnnotations());
        annotations.add(filter);
        //annotations.add(new AnnotationLiteral<OSGiService>() {});
        annotations.add(new OSGiServiceQualifier(parameter.getAnnotation(OSGiService.class).value()));
        if(parameter.getAnnotation(Required.class) != null) {
            annotations.add(new AnnotationLiteral<Required>() {});
        }
        for(Annotation annotation : parameter.getAnnotations()) {
            if(!annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                annotations.add(annotation);
            }
        }
    }

    @Override
    public int getPosition() {
        return parameter.getPosition();
    }

    @Override
    public AnnotatedCallable<T> getDeclaringCallable() {
        return parameter.getDeclaringCallable();
    }

    @Override
    public Type getBaseType() {
        return parameter.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return parameter.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) {
                return (T) annotation;
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
