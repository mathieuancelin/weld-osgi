package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

public class CDIOSGiInjectionPoint implements InjectionPoint{

    private Set<Annotation> qualifiers;
    private Filter filter;
    private Boolean required;
    private Type type;
    private Bean<?> bean;
    private Member member;
    private Annotated annotated;

    public CDIOSGiInjectionPoint(InjectionPoint injectionPoint, Set<Annotation> qualifiers, Filter filter, boolean required) {
        this.qualifiers = qualifiers;
        this.filter = filter;
        this.required = required;
        type = injectionPoint.getType();
        bean = injectionPoint.getBean();
        member = injectionPoint.getMember();
        annotated = injectionPoint.getAnnotated();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Bean<?> getBean() {
        return bean;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    public boolean isDelegate() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public int hashCode() {
        return getType().hashCode() + filter.hashCode() + required.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectionPoint)) return false;

        InjectionPoint that = (InjectionPoint) o;
        return hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return getMember().getName() +
                " with qualifiers: " +
                printQualifiers();
    }

    public String printQualifiers() {
        String result = "|";
        for(Annotation qualifier : getQualifiers()) {
            result += "@" + qualifier.annotationType().getSimpleName() + "|";
        }
        return result;
    }
}
